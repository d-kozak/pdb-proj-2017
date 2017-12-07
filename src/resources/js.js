function VMBridge() {};

VMBridge.prototype = {
	_bridgeUp: "VeriBridge",
	
	vm: null,
	// draw: function(x, y) {
		// this.vm.log("drawing: " + x + ", " + y)
		// L.marker(Point(x, y)).addTo(map)
	// },
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
		y
		ent.on("click", function() {
			self.vm.log(geom.focus())
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
		if(this.vm._bridgeUp == this._bridgeUp)
			this.vm.log("Bridge Java VM <- JS VM is up.")
		else // no bridge - no message
			this.vm.log(this.vm._bridgeUp)
	}
}

function Point(x, y) {
	return [48 + y / 100, 11 + x / 100] // [latitude, longitude]
}