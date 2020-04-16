

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

    setInterval(function() {
        displayAddresses(L, mymap);
    }, 10000);
}
let t1 = document.getElementById("gauge1");
let t2 = document.getElementById("gauge2");
let t3 = document.getElementById("gauge3");
let t4 = document.getElementById("gauge4");

let g1 = new Donut(t1);
let g2 = new Donut(t2);
let g3 = new Donut(t3);
let g4 = new Donut(t4);

let opts = {
    angle: 0.35, // The span of the gauge arc
    lineWidth: 0.1, // The line thickness
    radiusScale: 1, // Relative radius
    pointer: {
        length: 0.6, // // Relative to gauge radius
        strokeWidth: 0.035, // The thickness
        color: '#000000' // Fill color
    },
    limitMax: false,     // If false, max value increases automatically if value > maxValue
    limitMin: false,     // If true, the min value of the gauge will be fixed
    colorStart: '#17b632',   // Colors
    colorStop: '#0d7210',    // just experiment with them
    strokeColor: '#eeeeee',  // to see which ones work best for you
    generateGradient: true,
    highDpiSupport: true,
};

const icons = {
    grey : L.icon({
        iconUrl: '../resources/house-no.png',
        iconSize: [20, 20], // size of the icon
    }),
    green : L.icon({
        iconUrl: '../resources/house-yes.png',
        iconSize: [20, 20], // size of the icon
    }),
    falsepositive: L.icon({
        iconUrl: '../resources/house-false-positive.png',
        iconSize: [15, 15], // size of the icon
    }),
    falsenegative: L.icon({
        iconUrl: '../resources/house-false-negative.png',
        iconSize: [15, 15], // size of the icon
    }),
}


let falsePositive = 0;
let falseNegative = 0;
let truePositive = 0;
let trueNegative = 0;
let percentage = 0;
let total = 0;
async function displayAddresses(L, mymap){
        const data = await getData("addresses");

        console.log(data);

        if (data.length > 0) {
            data.map(address => {
                const lat = address.lat;
                const long = address.long;
                const averagePrice = address.average_price;
                const isWithinPopularArea = address.isWithinPopularArea;
                if (address.decision) {
                    if(((isWithinPopularArea === 1) && (averagePrice < 30 || averagePrice > 100)) || ((isWithinPopularArea === 0) && (averagePrice > 30 ))) {
                        falsePositive++;
                        setTimeout(function() {
                            L.marker([lat, long], {icon: icons.falsepositive}).addTo(mymap).bindPopup(`Potential Address but should be Non-Potential- ${lat}, ${long} - Price [${averagePrice}] - IsWithin[${isWithinPopularArea}]`);
                        }, Math.random()*2000)
                    }else{
                        truePositive++;
                        setTimeout(function () {
                            L.marker([lat, long], {icon: icons.green}).addTo(mymap).bindPopup(`Potential Address - ${lat}, ${long} - Price [${averagePrice}] - IsWithin[${isWithinPopularArea}]`);
                        }, Math.random()*2000)

                    }
                     // L.circle([lat, long], 40, {
                    //     color: '#52e00b',
                    //     fillColor: '#52e00b',
                    //     fillOpacity: 1
                    // }).addTo(mymap).bindPopup("Potential Address")
                } else {
                    if(((isWithinPopularArea === 1) && (averagePrice >30 && averagePrice < 100)) || ((isWithinPopularArea === 0) && (averagePrice < 30 ))) {
                        falseNegative++;
                        setTimeout(function() {
                            L.marker([lat, long], {icon: icons.falsenegative}).addTo(mymap).bindPopup(`Non-Potential Address but should be Potential- ${lat}, ${long} - Price [${averagePrice}] - IsWithin[${isWithinPopularArea}]`);
                        }, Math.random()*2000)

                    }else{
                        trueNegative++;
                        setTimeout(function(){
                            L.marker([lat, long], {icon: icons.grey}).addTo(mymap).bindPopup(`Non-Potential Address - ${lat}, ${long} - Price [${averagePrice}] - IsWithin[${isWithinPopularArea}]`);
                        }, Math.random()*2000)

                    }
                    // L.circle([lat, long], 40, {
                    //     color: '#939360',
                    //     fillColor: '#939360',
                    //     fillOpacity: 1
                    // }).addTo(mymap).bindPopup("Non-Potential Address");
                }
                // L.circle([lat, long], 200, options).addTo(mymap)
            })
            total = truePositive+trueNegative+falseNegative+falsePositive;
            percentage = (truePositive+trueNegative)/(total) *100
            g1.setOptions(opts).animationSpeed = 32;
            g1.setTextField(document.getElementById("preview-textfield-g1"));
            g1.maxValue = total;
            g1.set(truePositive);
            g2.setOptions(opts).animationSpeed = 32;
            g2.setTextField(document.getElementById("preview-textfield-g2"));
            g2.maxValue = total;
            g2.set(trueNegative);
            g3.setOptions(opts).animationSpeed = 32;
            g3.setTextField(document.getElementById("preview-textfield-g3"));
            g3.maxValue = total;
            g3.set(falsePositive);
            g4.setOptions(opts).animationSpeed = 32;
            g4.setTextField(document.getElementById("preview-textfield-g4"));
            g4.maxValue = total;
            g4.set(falseNegative);
            // alert(percentage);
        }
    // }
}

async function displayListings(L, mymap){
    const data = await getData("listings");

    for (let elem in data) {
        const lat = data[elem].lat;
        const long = data[elem].long;
        const decision = data[elem].decision;
        let value = "Is-Valid";
        const averagePrice = data[elem].average_price;
        const options = {
            color: '#966a6a',
            fillColor: '#966a6a',
            fillOpacity: 0.5
        };
        if(decision==0){
            value = "Invalid"
            options.color="#ab9dab"
            options.fillColor="#ab9dab"
        }
        // setTimeout(function() {
            L.circle([lat, long], 5, options).addTo(mymap).bindPopup(`${lat}-${long}, Price - ${averagePrice}, Decision - ${value}`)
        // }, Math.random()*2000)
    }
}

async function displayPopularArea(L, mymap){
    const data = await getData("popularArea");

    for (let elem in data) {
        const lat = data[elem].lat;
        const long = data[elem].long;
        const address = data[elem].address;

        L.marker([lat, long]).addTo(mymap)
            .bindPopup(address);
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
        .then(res => res)
        .catch(err => {error: err});

    return data;
}

