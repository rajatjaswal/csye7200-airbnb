

function loadMap(){
    var mymap = L.map('mapid').setView([-37.8136, 144.9631], 10);

    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
        maxZoom: 18,
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
            '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
            'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        id: 'mapbox/streets-v11',
        tileSize: 512,
        zoomOffset: -1
    }).addTo(mymap);

    L.circle([-37.8136, 144.9631], 10, {
        color: 'red',
        fillColor: '#f03',
        fillOpacity: 0.2
    }).addTo(mymap).bindPopup("I am a circle.");

    var popup = L.popup();

    function onMapClick(e) {
        popup
            .setLatLng(e.latlng)
            .setContent("You clicked the map at " + e.latlng.toString())
            .openOn(mymap);
    }

    mymap.on('click', onMapClick);



    displayListings(L, mymap);
    displayPopularArea(L, mymap);
    displayAddresses(L, mymap);
}

async function displayAddresses(L, mymap){
    const data = {};
    const addressSocket = new WebSocket(
        'ws://localhost:3800/airbnb-service/addresses'
    );
    addressSocket.onmessage = (event) => {
        let data = JSON.parse(event.data);
        const lat = data.lat;
        const long = data.long;

        L.circle([lat, long], 10, {
            color: '#a504f5',
            fillColor: '#a504f5',
            fillOpacity: 0.05
        }).addTo(mymap)
    }

}

async function displayListings(L, mymap){
    const data = await getData("listings");

    for (let elem in data) {
        const lat = data[elem].lat;
        const long = data[elem].long;
        const decision = data[elem].decision;
        const options = {
            color: 'red',
            fillColor: '#f03',
            fillOpacity: 0.2
        };
        if(decision==0){
            options.color="blue"
            options.fillColor="#3197ff"
        }
        L.circle([lat, long], 10, options).addTo(mymap)
    }
}

async function displayPopularArea(L, mymap){
    const data = await getData("popularArea");

    for (let elem in data) {
        const lat = data[elem].lat;
        const long = data[elem].long;
        const address = data[elem].address;
        const options = {
            color: 'red',
            fillColor: '#38ff19',
            fillOpacity: 0.7
        };
        L.circle([lat, long], 200, options).addTo(mymap)

        L.marker([lat, long]).addTo(mymap)
            .bindPopup(address).openPopup();
    }
}

loadMap();

async function getData(type) {

    var backendHost = "http://localhost:3700";
    var backendRoute = `${backendHost}/airbnb-service/${type}`;
    const headers = {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'OPTIONS, POST, GET',
        'Access-Control-Max-Age': 2592000,
        'Content-Type': 'application/json',// 30 days
    };

    const data = await fetch(backendRoute)
        .then(data => {
            return data.json()
        })
        .then(res => res);

    return data;
}

