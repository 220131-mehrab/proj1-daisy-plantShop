var welcomeMsg = 'Welcome to the Plant Nursery!';
document.querySelector('h1').innerText = welcomeMsg;

fetch('/plants').then(resp => resp.json()).then(plants => {
    document.querySelector('#plants').innerHTML = listPlants(plants);
    }
);

function listPlants(json) {
    return `${json.map(listPlant)}`;
};

let listPlant = function(plant) {
    return '<p>' + plant.plantId + ": " + plant.name + '</p>';
};

function postPlant() {
    let plant = {
        "plantId":document.getElementById("plantId").value,
        "name": document.getElementById("name").value
    }
    console.log(plant);
    fetch("/plants", {
        method: "POST",
        headers:{
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(plant)
    }).then((result) => {
        if (result.status != 200) {
            throw new Error("Bad Server Response");
        }
        console.log(result.text());
    }).catch((error) => { console.log(error); })
    fetch('/plants').then(resp => resp.json()).then(plants => {
        document.querySelector('#plants').innerHTML = listPlants(plants);
    });
}