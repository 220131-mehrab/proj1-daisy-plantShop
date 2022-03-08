var welcomeMsg = 'Welcome to the Plant Shop!'

document.querySelector('h1').innerText = welcomeMsg

fetch('/plants').then(resp => resp.json()).then(plants => {
    document.querySelector('body').innerHTML = listPlants(plants);
    }
);

let listPlants = function(plant) {
    return '<p>' + plants.plantId + ": " + plant.name + '</p>';
}

function listPlants(json) {
    return `
        <div id="plantsList">
            ${json.map(listPlants).join('/n')}
        </div>
    `
}



// function get(url) {
//     return new Promise(function (resolve, reject) {
//         var req = new XMLHttpRequest();
//         req.open('GET', '/plants');
//         req.onload = function() {
//             if (req.status == 200) {
//                 resolve(req.response);
//             } else {
//                 reject(Error(req.statusText));
//             }
//         }
//         req.onerror = function() {
//             reject(Errpr("Network error"));
//         }
//         req.send();
//     })
// }
// get('/plants').then(function(response) {
//     console.log(JSON.parse(response));
// }), function (err) {

// }


// function postPlant() {
//     let plant = new Plant();
//     plant.append("plantId", document.getElementById("plant.name"),value);
//     plant.append("name", document.getElementById("name").value);

//     fetch("/plants". {
//         method: "POST",
//         body: plant
//     }).then((result) => {
//         if (result.status != 200) {
//             throw new Error("Bad Server Response");
//         }
//         console.log(result.text());
//     }).catch((error) => { console.log(error); })
//     return false;
// }