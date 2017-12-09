function VMBridge() {};

VMBridge.prototype = {
	_bridgeUp: "VeriBridge",
	vm: null,
	
	clearAll: function() {
		geoEntities.removeLayer()
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
				desc = geom.getDescription()
				pt = desc.getCenter()
				ent = L.circle(Point(pt.getX(), pt.getY()), {radius: desc.getRadius()})
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
					self.vm.log("testuju")
					if(layer instanceof L.Marker) {
						var dpt = dePoint(layer.getLatLng())
						
						layer._javaEnt.updatePointGeometry(dpt[0], dpt[1])
						self.vm.log(":" + dpt)
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

function dePoint(xy) {
	return [(xy.lng - 11) * 100, (xy.lat - 48) * 100] // [longitude, latitude]
}