var container;
var camera, controls, scene, renderer, meshGeo;
//var width = window.innerWidth;
//var height = window.innerHeight;

init();
render();

function animate() {
    requestAnimationFrame(animate);
    controls.update();
}

function init() {
    camera = new THREE.PerspectiveCamera(
        45,         // Field of view
        window.innerWidth / window.innerHeight,  // Aspect ratio
        .1,         // Near
        20000       // Far
    );
    
    controls = new THREE.OrbitControls(camera);
    controls.damping = 0.2;
    controls.addEventListener('change', render);
    //controls.center.set(2614250, 369.85, 1260250); 
    
    scene = new THREE.Scene();
    scene.fog = new THREE.FogExp2( 0xcccccc, 0.0000 );

    var sphereLight = new THREE.HemisphereLight(0xFFFAE3, 0x39321, 0.5);
    scene.add(sphereLight);
    
    renderer = new THREE.WebGLRenderer( { antialias: false } );
    renderer.setClearColor( scene.fog.color );
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.shadowMapEnabled = true;

    container = document.getElementById( 'container' );
    container.appendChild( renderer.domElement );
    
    window.addEventListener( 'resize', onWindowResize, false );

    $.when(loadLineData(), loadMeshData()).done(function(a1, a2) {
        centerControls(meshGeo);
        updateCamera(meshGeo);

        animate();
    });
}

function onWindowResize() {
    camera.aspect =  window.innerWidth/ window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight);

    render();
}

function render() {
    renderer.render( scene, camera );
}

function loadLineData() {
    var points = [];
    
    return $.getJSON( "res/contourlines.json", function( data ) {
         var material = new THREE.LineBasicMaterial({
            color: 0xFF3030
        });

        //console.log(data);
        var sumHoehe = 0;
        var geo = new THREE.Geometry();

        if(feats = data["features"]){
            ////console.log(feats);

            for (feat of feats) {
                ////console.log(feat)

                if(feat["type"] === "Feature" && feat["geometry"] !== null) {
                    var geom = feat["geometry"];
                    ////console.log(geom);
                    ////console.log(feat['properties']['HOEHE']);
                    //sumHoehe += feat['properties']['HOEHE'];

                    if(geom["type"] === "LineString" && geom["coordinates"] !== null) {
                        var linestr = geom["coordinates"]
                        ////console.log(linestr)
                        var geometry = new THREE.Geometry();

                        for (coord of linestr) {
                            ////console.log(coord);

                            //points.push(new THREE.Vector3(coord[0], coord[2], coord[1]));
                            //geometry.vertices.push(new THREE.Vector3(coord[0] - 2614250, coord[2] - 369.85, coord[1] -1260250));
                            //geometry.vertices.push(new THREE.Vector3(coord[0] - 2614250, coord[1] -1260250, coord[2] - 369.85));
                            //geometry.vertices.push(new THREE.Vector3(coord[0], coord[1], coord[2]));
                            //geometry.vertices.push(new THREE.Vector3(coord[0], coord[2], coord[1]));
                            geometry.vertices.push(new THREE.Vector3(coord[0], coord[2] + 1, coord[1]));
                        }

                        var line = new THREE.Line(geometry, material);
                        scene.add(line);
                    }
                }
            }

            ////console.log(sumHoehe / feats.length);
            /*var pointsShape = new THREE.Shape(points);
            var extrudeSettings = { amount: 8, bevelEnabled: true, bevelSegments: 2, steps: 2, bevelSize: 1, bevelThickness: 1 };
            
            var geo = new THREE.ExtrudeGeometry(pointsShape, extrudeSettings);
            var geoMesh = new THREE.Mesh(geo, new THREE.MeshPhongMaterial({color: 0xFF0033, ambient: 0xFF0033}));
            scene.add(geoMesh);*/
            
            //animate();
            //console.log(scene);
        }
    });
}

function loadMeshData() {
    return $.getJSON( "res/contourlines_tin.json", function( data ) {
        var material = new THREE.MeshPhongMaterial({
            color: 0xFF3030,
            ambient: 0xFF0033,
            side: THREE.DoubleSide
        });
        /*var material = new THREE.MeshBasicMaterial({
            color: 0xFF3030,
            side: THREE.DoubleSide
        });*/
        /*var material = new THREE.MeshNormalMaterial({
           side: THREE.DoubleSide
        });*/
        

        //console.log(data);
        var stopIndex = 0;
        
        var geometry = new THREE.Geometry();

        if(feats = data["features"]){
            ////console.log(feats);

            for (feat of feats) {
                ////console.log(feat)

                if(feat["type"] === "Feature" && feat["geometry"] !== null) {
                    var geom = feat["geometry"];

                    if(geom["type"] === "Polygon" && geom["coordinates"] !== null) {
                        var polygons = geom["coordinates"]
                        
                        //geometry.vertices.push(new THREE.Vector3(coord[0], coord[2], coord[1]));
                        
                        ////console.log(polygons);
                        
                        for (poly of polygons) {
                            var triangle = new THREE.Geometry();
                            
                            for (p2 of poly) {
                                ////console.log(p2);
                                
                                
                                //triangle.vertices.push(new THREE.Vector3(p2[0] - 2614250,p2[1] -1260250, p2[2] - 369.85 ));
                                //coord[0] - 2614250, coord[2] - 369.85, coord[1] -1260250)
                                //triangle.vertices.push(new THREE.Vector3(p2[0] - 2614250, p2[2] - 369.85, p2[1] -1260250));
                                triangle.vertices.push(new THREE.Vector3(p2[0], p2[2], p2[1]));
                            }
                            
                            triangle.faces.push( new THREE.Face3( 0, 1, 2 ) );
//                            triangle.computeFaceNormals();
//                            var triaMesh = new THREE.Mesh(triangle, material);
//                            //scene.add(triaMesh);
//                            THREE.GeometryUtils.merge(geometry, triaMesh);
                            
                            THREE.GeometryUtils.merge(geometry, triangle);
                        }
                    }
                }
            }

            geometry.computeFaceNormals();
            //var mesh = new THREE.Mesh( geometry, material );
            //scene.add(mesh);
            loadTexture(geometry);
            
            //updateCamera(geometry);
            //console.log(camera);
            /*var material2 = new THREE.MeshPhongMaterial({
                map: THREE.ImageUtils.loadTexture('res/img/10_raster2.png')
            });

            var mesh2 = new THREE.Mesh(geometry, material2);
            scene.add(mesh2);*/
            
            //animate();
            //console.log(scene);
            meshGeo = geometry;
        }
    });
}

function loadTexture(geometry) {
    //console.log("loadTexture");
    
    //var geo2 = new THREE.PlaneGeometry(600, 600, 1 ,1);
    
    
    //var geo = new THREE.Geometry();
    /*geo.vertices.push(new THREE.Vector3(2614000, 350, 1260000));
    geo.vertices.push(new THREE.Vector3(2614000, 350, 1260500));
    geo.vertices.push(new THREE.Vector3(2614500, 350, 1260500));
    geo.vertices.push(new THREE.Vector3(2614500, 350, 1260000));*/
    /*geo.vertices.push(new THREE.Vector3(-300, -300, 0));
    geo.vertices.push(new THREE.Vector3(-300, 300, 0));
    geo.vertices.push(new THREE.Vector3(300, 300, 0));
    geo.vertices.push(new THREE.Vector3(300, -300, 0));*/
    /*geo.vertices.push(new THREE.Vector3(-250, 0, -250));
    geo.vertices.push(new THREE.Vector3(-250, 0, 250));
    geo.vertices.push(new THREE.Vector3(250, 0, 250));
    geo.vertices.push(new THREE.Vector3(250, 0, -250));
    
    geo.faces.push(new THREE.Face3(0, 1, 2));
    geo.faces.push(new THREE.Face3(2, 3 ,0));
    
    geo.computeFaceNormals();*/
    
    //assignUVs(geo);
    assignUVs(geometry);
    ////console.log(geo.faceVertexUvs[0]);
    //console.log(geometry.faceVertexUvs[0]);

    var texture = THREE.ImageUtils.loadTexture('res/img/orthophoto.png', {}, function()  {
        render();
    });
    
    texture.needsUpdate = true;

    var material2 = new THREE.MeshPhongMaterial({
        //map: THREE.ImageUtils.loadTexture('res/img/10_raster.png'),
        map: texture,
        shininess: 0,
        emissive: 0x787878,
        vertexColors: 0xffffff
        //map: THREE.ImageUtils.loadTexture('http://geowms.bl.ch/?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&BBOX=2614000,1260000,2614500,1260500&LAYERS=orthofotos_agi_2012&STYLES=&SRS=EPSG%3A2056&FORMAT=image%2Fpng&TRANSPARENT=FALSE&WIDTH=250&HEIGHT=250'),
        //color: 0xF72F2F,
        //side: THREE.DoubleSide
    });
    
    //var mesh2 = new THREE.Mesh( geo2, material2 );
    var mesh2 = new THREE.Mesh( geometry, material2 );
    //var mesh2 = new THREE.Mesh(geo, material2);
    //var mesh2 = new THREE.Mesh(cube, material2);
    //mesh2.overdraw = true;
    //console.log(mesh2.position);
    scene.add(mesh2);
    //mesh2.position.set(2614250, 320.85, 1260250);
    //console.log(mesh2.position);
}

function assignUVs(geometry){
    geometry.computeBoundingBox();

    var max     = geometry.boundingBox.max;
    var min     = geometry.boundingBox.min;

    var offset  = new THREE.Vector2(0 - min.x, 0 - min.z);
    var range   = new THREE.Vector2(max.x - min.x, max.z - min.z);

    geometry.faceVertexUvs[0] = [];
    var faces = geometry.faces;

    for (i = 0; i < geometry.faces.length ; i++) {

      var v1 = geometry.vertices[faces[i].a];
      var v2 = geometry.vertices[faces[i].b];
      var v3 = geometry.vertices[faces[i].c];

      geometry.faceVertexUvs[0].push([
        new THREE.Vector2( ( v1.x + offset.x ) / range.x , ( v1.z + offset.y ) / range.y ),
        new THREE.Vector2( ( v2.x + offset.x ) / range.x , ( v2.z + offset.y ) / range.y ),
        new THREE.Vector2( ( v3.x + offset.x ) / range.x , ( v3.z + offset.y ) / range.y )
      ]);

    }

    geometry.uvsNeedUpdate = true;
}

//Methode zum gute Kamera-Perspektive zu erhalten.
function updateCamera(geometry) {
    geometry.computeBoundingBox();
    var boundingBox = geometry.boundingBox;
    
    var height = boundingBox.max.z - boundingBox.min.z;

    var width = boundingBox.max.x - boundingBox.min.x;
    var vertical_FOV = camera.fov * (Math.PI/ 180);

    var max_z = boundingBox.max.y;
    //console.log(max_z);

    var apsect = window.innerWidth/window.innerHeight;
    //console.log(apsect);

    var horizontal_FOV = 2 * Math.atan (Math.tan (vertical_FOV/2) * apsect);
    //console.log(horizontal_FOV);

    var distance_vertical = height / (2 * Math.tan(vertical_FOV/2));
    //console.log(height);
    //console.log(2 * Math.tan(vertical_FOV/2));
    //console.log("Distance Vertical:" + distance_vertical);
    // alert ('vertical' + distance_vertical);
    var distance_horizontal = width / (2 * Math.tan(horizontal_FOV/2));
    //console.log(distance_horizontal);
    // alert ('horizontal' + distance_horizontal);
    var z_distance = distance_vertical >= distance_horizontal? distance_vertical : distance_horizontal;

    camera.position.y = z_distance + max_z;
    camera.position.x = (boundingBox.min.x + boundingBox.max.x) / 2;
    camera.position.z = (boundingBox.min.z + boundingBox.max.z) / 2;
    //camera.position.x = 2614250;
    //camera.position.z = 1260250;
}

function centerControls(geometry) {
    geometry.computeBoundingBox();
    var boundingBox = geometry.boundingBox;
    var midZ = (boundingBox.min.z + boundingBox.max.z) / 2
    var midX = (boundingBox.min.x + boundingBox.max.x) / 2
    var midY = (boundingBox.min.y + boundingBox.max.y) / 2

    controls.center.set(midX, midY, midZ);
    console.log(controls);
}