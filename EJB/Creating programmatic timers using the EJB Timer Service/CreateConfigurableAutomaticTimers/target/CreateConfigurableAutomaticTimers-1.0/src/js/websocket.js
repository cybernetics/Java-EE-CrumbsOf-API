var output;

function init() {
    if (window.WebSocket) {
        output = document.getElementById("response");

        websocket = new WebSocket("ws://localhost:8080/ConfigurableAutomaticTimers/scheduler");
        websocket.onopen = function (evt) {
            onOpen(evt);
        };
        websocket.onclose = function (evt) {
            onClose(evt);
        };
        websocket.onerror = function (evt) {
            onError(evt);
        };
        websocket.onmessage = function (evt) {
            onMessage(evt);
        };
    } else {
        alert("Your browser does not support WebSockets!");
    }
}

function onOpen(evt) {
    writeToScreen("<span class='default'>CONNECTED</span>");
    doSend("Test connection..", "WebSockets are supported!");
}

function onClose(evt) {
    writeToScreen("<span class='default'>DISCONNECTED</span>");
}

function onError(evt) {
    writeToScreen("<span class='error'>ERROR: </span> " + evt.data);
}

function onMessage(evt) {
    writeToScreen(evt.data);
}

function doSend(message, data) {
    writeToScreen("<span class='primary'>ME: " + message + "</span>");
    websocket.send(data);
}

function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
    output.scrollTop = output.scrollHeight;
}

window.addEventListener("load", init, false);
