function VMBridge() {};

VMBridge.prototype = {
	_bridgeUp: "VeriBridge",
	vm: null,
	drawColor: null,
	
	clearAll: function() {
		geoEntities.clearLayers()
	},
	setColor: function(color) { // sets color for all shapes
		var ents = ['rectangle', 'polygon', 'polyline', 'circle'],
			options = {},
			setting = {
				shapeOptions: {
					color: color
				}
			}
			
		ents.forEach(function(ent) {
			options[ent] = setting
		})
		DrawControl.setDrawingOptions(options)
	},
	
	draw: function(javaEnt) {
		var geom = javaEnt.getGeometry(),
			desc,
			pt, pts, ent,
			x, y, self = this
			
		this.vm.log("draw: " + geom.getType())
		
		switch(String(geom.getType())) {
			case "POINT":
				desc = geom.getDescription()
				x = desc.getX(); y = desc.getY()
				ent = L.marker(Point(x, y))
				// this.vm.log("drawing: " + x + ", " + y)
				break;
				
			case "LINE":
			case "POLYGON":
				desc = geom.getPtArr()
				pts = []
				for(var i = 0; i < desc.length; i += 2) {
					pts.push(Point(desc[i], desc[i+1]))
				}
				// this.vm.log("drawing: " + pts)
				if(geom.getType() == "LINE")
					ent = L.polyline(pts)
				else
					ent = L.polygon(pts)
				break
				
			case "CIRCLE":
				desc = geom.getDescription() // [Point, Java DoubleProperty]
				pt = desc[0]
				ent = L.circle(Point(pt.getX(), pt.getY()), {radius: Radius(desc[1].get())})
		}
		
		ent._javaEnt = javaEnt
		ent.on("click", function(e) {
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
			
			// Layer editation
			map.on(L.Draw.Event.EDITED, function (e) {
				e.layers.eachLayer(function (layer) {
					var coords, coords2, i, dpt
					
					if(layer instanceof L.Marker) {
						dpt = dePoint(layer.getLatLng())
						
						layer._javaEnt.updatePointGeometry(dpt[0], dpt[1])
						self.vm.log(":" + dpt)
					} else if(layer instanceof L.Circle) {
						dpt = dePoint(layer.getLatLng())
						
						layer._javaEnt.updateCircleGeometry(dpt[0], dpt[1], deRadius(layer.getRadius()))
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