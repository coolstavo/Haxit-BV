var events = initialEvents; // Use the global variable passed from Thymeleaf
var markers = {};
var eventIndex = events.length;

// Groningen and Drenthe
var maxBounds = L.latLngBounds(
    L.latLng(52.9, 6.0),   
    L.latLng(53.6, 7.3)   
);

var map = L.map('map', {
    maxBounds: maxBounds,
    maxBoundsViscosity: 1.0
}).setView([53.2194, 6.5665], 13);

map.setMaxBounds(maxBounds);

// Oude map 
// Removed the L.mapkitMutant layer
// var roads = L.mapkitMutant({
// 	type: 'hybrid', // valid values are 'default', 'satellite' and 'hybrid'
// 	language: 'en',
// 	debugRectangle: false
// }).addTo(map);

var Stadia_AlidadeSmoothDark = L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.{ext}', {
	minZoom: 0,
	maxZoom: 20,
	ext: 'png'
}).addTo(map);

var customIcon = L.icon({
    iconUrl: pinIconUrl,
    iconSize: [15, 40],
    iconAnchor: [15, 40],
    popupAnchor: [-6, -40],
    className: 'custom-div-icon'
});


if (Array.isArray(events) && events.length > 0) {
    events.forEach(function(event, index) {
        var lat = Number(event.lat);
        var lng = Number(event.lng);
        if (!Number.isFinite(lat) || !Number.isFinite(lng)) return;

        var marker = L.marker([lat, lng], {icon: customIcon}).addTo(map);
        var popupText = "<b>" + event.title + "</b><br>" + 
                        "" + event.description + "<br>" +
                        "<span class='badge rounded-pill navbar-brown'>" + event.type + "</span>";
        marker.bindPopup(popupText);
        
        markers[index] = marker;
    });
} else {
    console.warn("Geen evenementen gevonden om weer te geven op de kaart.");
}

function focusOnMap(lat, lng, index) {
    var nLat = Number(lat);
    var nLng = Number(lng);
    if (Number.isFinite(nLat) && Number.isFinite(nLng)) {
        map.flyTo([nLat, nLng], 15);
        
        if (markers[index]) {
            setTimeout(function() {
                markers[index].openPopup();
            }, 500);
        }
    }
}

// click op map voor coordinaten! dan toevoegen clicken
map.on('click', function(e) {
    document.getElementById('eventLat').value = e.latlng.lat.toFixed(6);
    document.getElementById('eventLng').value = e.latlng.lng.toFixed(6);
});

document.getElementById('addEventForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    var formData = {
        title: document.getElementById('eventTitle').value,
        description: document.getElementById('eventDescription').value,
        type: document.getElementById('eventType').value,
        lat: parseFloat(document.getElementById('eventLat').value),
        lng: parseFloat(document.getElementById('eventLng').value)
    };

    // Send to backend
    fetch('/api/events/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
    .then(response => response.json())
    .then(data => {
        // toevoegen aan map
        var marker = L.marker([data.lat, data.lng], {icon: customIcon}).addTo(map);
        var popupText = "<b>" + data.title + "</b><br>" + 
                        "" + data.description + "<br>" +
                        "<span class='badge rounded-pill navbar-brown'>" + data.type + "</span>";
        marker.bindPopup(popupText);
        markers[eventIndex] = marker;

        // toevoegen aan lijst
        var eventCard = document.createElement('a');
        eventCard.href = '#';
        eventCard.className = 'list-group-item list-group-item-action event-card border-0 shadow-sm mb-2 rounded';
        eventCard.dataset.lat = data.lat;
        eventCard.dataset.lng = data.lng;
        eventCard.dataset.index = eventIndex;
        eventCard.onclick = function() {
            focusOnMap(this.dataset.lat, this.dataset.lng, this.dataset.index);
        };
        eventCard.innerHTML = `
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1 text-dark">${data.title}</h5>
                <span class="badge rounded-pill navbar-brown">${data.type}</span>
            </div>
            <p class="mb-1">${data.description}</p>
        `;
        document.querySelector('.list-group').appendChild(eventCard);

        eventIndex++;

        bootstrap.Modal.getInstance(document.getElementById('addEventModal')).hide();
        document.getElementById('addEventForm').reset();

        map.flyTo([data.lat, data.lng], 15);
        setTimeout(function() {
            marker.openPopup();
        }, 500);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Er is een fout opgetreden bij het toevoegen van het event.');
    });
});
