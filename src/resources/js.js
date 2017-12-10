function VMBridge() {};

VMBridge.prototype = {
	_bridgeUp: "VeriBridge",
	vm: null,
	drawColor: null,
	selLayer: null,
	
	highlight: function(layer) {
		// highligh selected entity
		if(this.selLayer)
			this.selLayer.setStyle({opacity: 0.75})
		layer.setStyle({opacity: 1})
		this.selLayer = layer
	}, _highlight: function(layer) {
		var self = this
		return function() {self.highlight.call(self, layer)}
	},
	
	clearAll: function() {
		geoEntities.clearLayers()
	},
	setColor: function(color) { // sets color for all shapes
		var ents = ['rectangle', 'polygon', 'polyline', 'circle'],
			options = {},
			setting = {
				shapeOptions: {
					color: color,
					opacity: 0.75
				}
			}
			
		ents.forEach(function(ent) {
			options[ent] = setting
		})
		DrawControl.setDrawingOptions(options)
		this.vm.log("Selecting drawing color " + color)
	},
	
	draw: function(javaEnt, color) {
		var geom = javaEnt.getGeometry(),
			desc, col = {color: color, opacity: .75},
			pt, pts, ent,
			x, y, self = this
			
		this.vm.log("draw: " + geom.getType())
		
		switch(String(geom.getType())) {
			case "POINT":
				desc = geom.getDescription()
				x = desc.getX(); y = desc.getY()
				ent = L.marker(Point(x, y), col)
				// this.vm.log("drawing: " + x + ", " + y)
				break;
				
			case "LINE":
			case "RECTANGLE":
			case "POLYGON":
				desc = geom.getPtArr()
				pts = []
				for(var i = 0; i < desc.length; i += 2) {
					pts.push(Point(desc[i], desc[i+1]))
				}
				// this.vm.log("drawing: " + pts)
				if(geom.getType() == "LINE")
					ent = L.polyline(pts, col)
				else if(geom.getType() == "RECTANGLE")
					ent = L.rectangle(pts, col)
				else
					ent = L.polygon(pts, col)
				break
				
			case "CIRCLE":
				desc = geom.getDescription() // [Point, Java DoubleProperty]
				pt = desc[0]
				col.radius = Radius(desc[1].get())
				ent = L.circle(Point(pt.getX(), pt.getY()), col)
				break
		}
		
		ent._javaEnt = javaEnt
		javaEnt.setLayer({highlight: self._highlight(ent)})
		
		ent.on("click", self._highlight(ent))
		ent.on("click", function(e) {
			// select entity on Java GUI
			javaEnt.select()
		}).addTo(geoEntities)
	},
	check: function(geom) {
				str = ""
				for(var p in geom)
				{
					str += p + ": " + geom[p]
				}
				this.vm.log(" " + str)
	},
	
	running: function() {
		var self = this
		
		if(this.vm._bridgeUp == this._bridgeUp) {
			this.vm.log("Bridge Java VM <- JS VM is up.")
			
			// and now initialize WebView
			// Map click
			map.on("click", function(e) {
				var dpt = dePoint(e.latlng)
				
				//self.vm.log("You clicked the map at " + e.latlng + " " + dePoint(e.latlng))
				self.vm.clickEvent(dpt[0], dpt[1])
			})
			
			// Editation of geometry
			map.on(L.Draw.Event.EDITED, function (e) {
				e.layers.eachLayer(function (layer) {
					var coords, coords2, i, dpt
					
					if(layer instanceof L.Marker) {
						dpt = dePoint(layer.getLatLng())
						
						layer._javaEnt.updatePointGeometry(dpt[0], dpt[1])
					} else if(layer instanceof L.Circle) {
						dpt = dePoint(layer.getLatLng())
						
						layer._javaEnt.updateCircleGeometry(dpt[0], dpt[1], deRadius(layer.getRadius()))
					} else if(layer instanceof L.Rectangle) {
						coords2 = []
						coords = layer.getLatLngs()[0]
					
						for(i = 0; i < coords.length; i += 2) { // every other points of rectangle (one of diagonals)
							dpt = dePoint(coords[i])
							coords2.push(dpt[0], dpt[1])
						}
						
						layer._javaEnt.updateStringGeometry(JSON.stringify(coords2))
					} else if(layer instanceof L.Polyline) {
						coords2 = []
						coords = layer.getLatLngs()
						if(layer instanceof L.Polygon) // [R-space, R-cycle]
							coords = coords[0]
						
						for(i = 0; i < coords.length; i++) {
							dpt = dePoint(coords[i])
							coords2.push(dpt[0], dpt[1])
						}
						
						self.vm.log(JSON.stringify(coords2))
						layer._javaEnt.updateStringGeometry(JSON.stringify(coords2))
					}
			
				})
			})
			
			// Entity deletion
			map.on(L.Draw.Event.DELETED, function (e) {
				e.layers.eachLayer(function (layer) {

					self.vm.removeEntity(layer._javaEnt)
				})
			})
			
			// Entity creation
			map.on(L.Draw.Event.CREATED, function (e) {
				var coords, coordes = [], i, dpt, ent,
					layer = e.layer
				
				if(layer instanceof L.Marker) {
					dpt = dePoint(layer.getLatLng())
					
					ent = self.vm.addPoint(dpt[0], dpt[1])
				} else if(layer instanceof L.Circle) {
					dpt = dePoint(layer.getLatLng())
					
					ent = self.vm.addCircle(dpt[0], dpt[1], deRadius(layer.getRadius()))
				}
				else if(layer instanceof L.Rectangle)
				{
					coords = layer.getLatLngs()[0]
					
					for(i = 0; i < coords.length; i += 2) { // every other points of rectangle (oen of diagonals)
						dpt = dePoint(coords[i])
						coordes.push(dpt[0], dpt[1])
					}
					
					self.vm.addRectangle(coordes[0], coordes[1], coordes[2], coordes[3])
				} else if(layer instanceof L.Polyline) {
					coordes = []
					coords = layer.getLatLngs()
					if(layer instanceof L.Polygon) // [R-space, R-cycle]
						coords = coords[0]
					
					for(i = 0; i < coords.length; i++) {
						dpt = dePoint(coords[i])
						coordes.push(dpt[0], dpt[1])
					}
					
					coordes = JSON.stringify(coordes)
					if(layer instanceof L.Polygon)
						ent = self.vm.addPolygon(coordes)
					else
						ent = self.vm.addLine(coordes)
				}
				
				layer._javaEnt = ent
				layer.on("click", self._highlight(layer))
				ent.setLayer({highlight: self._highlight(layer)})
			})
			
		} else // no bridge - no message
			this.vm.log(this.vm._bridgeUp)
	}
}

function Point(x, y) {
	return [48 + y / 100, 11 + x / 100] // [latitude, longitude]
}

function Radius(r) {
	return r*100
}

function dePoint(xy) {
	return [(xy.lng - 11) * 100, (xy.lat - 48) * 100] // [longitude, latitude]
}

function deRadius(r) {
	return r/100
}