// Initialize the STOMP client
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ironoc-ws',
//    connectHeaders: {
//        login: username, // Optional, if authentication is required
////            passcode: 'your-password', // Optional, if authentication is required
//        id: username // Add your custom ID here
//    },
    debug: (str) => {
        console.log(str); // Debugging logs
    },
    reconnectDelay: 5000, // Reconnect after 5 seconds if the connection is lost
});

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#send").prop("send", connected);

    $("#disconnect").prop("disabled", !connected);

    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
    $("#messages").html("");
}

function connect() {
    stompClient.activate();

    stompClient.onConnect = (frame) => {
        setConnected(true);
        stompClient.subscribe('/topic/broadcast/user', (greeting) => {
        whoami = frame.headers['user-name'];
                     console.info('User ID: ' + whoami);
            showGreeting(JSON.parse(greeting.body).content);
        });

        // Subscribe to user-specific queue
        stompClient.subscribe('/user/queue/messages',//-'
//            + $("#name").val(),
            function (message) {
             console.info('Headers: ' + frame);
             whoami = frame.headers['user-name'];
             console.info('User ID: ' + whoami);
            const content = message.body;
//            showMessage(JSON.parse(content).content);
            showMessage(content);
        });
    };
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'name': $("#name").val()})
    });
}

function sendToUser() {
    stompClient.publish({
        destination: "/app/send-to-user",
        body: JSON.stringify({'username': $("#username").val(),
        'targetName': $("#targetName").val(),
        'content':  $("#content").val()})
    });
}

function showGreeting(greeting) {
    $("#greetings").append("<tr><td>" + greeting + "</td></tr>");
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#registerSession" ).click(() => sendName());
    $( "#sendToUser" ).click(() => sendToUser());
});

