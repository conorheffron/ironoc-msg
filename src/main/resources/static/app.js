// Initialize the STOMP client
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ironoc-ws',
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
    $("#broadcastMessages").html("");
    $("#privateMessages").html("");
}

function connect() {
    stompClient.activate();

    stompClient.onConnect = (frame) => {
        let whoami = frame.headers['user-name'];
        setLoggedInUser("loggedInUser", whoami);
        setConnected(true);

        stompClient.subscribe('/topic/broadcast/user', (broadcastMessage) => {
            showBroadcast(JSON.parse(broadcastMessage.body).content);
        });

        // Subscribe to user-specific queue
        stompClient.subscribe('/user/queue/messages',
            function (message) {
                console.info('Message: ' + message.body);
                console.info('Headers: ' + frame);
                whoami = frame.headers['user-name'];
                let msgJson = JSON.parse(message.body);
                const content = msgJson.content;
                const from = msgJson.sender;
                const to = msgJson.targetName;
                showMessage(whoami, from, to, content);
        });
    };
}

// Function to safely set innerHTML
function setLoggedInUser(elementId, userIdValue) {
    try {
        if (typeof elementId !== "string" || typeof userIdValue !== "string") {
            throw new Error("Invalid arguments: elementId and userIdValue must be strings.");
        }

        const element = document.getElementById(elementId);
        if (!element) {
            throw new Error(`Element with id "${elementId}" not found.`);
        }

        // Escape HTML special characters to prevent injection
        const safeUserId = userIdValue.replace(/[&<>"']/g, match => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        }[match]));

        element.innerHTML = `Logged in as: <strong><span id="safeUserId">${safeUserId}</span></strong>`;
    } catch (err) {
        console.error(err.message);
    }
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/broadcast",
        body: JSON.stringify({'name': document.getElementById("safeUserId").textContent})
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

function showBroadcast(broadcastMessage) {
    $("#broadcastMessages").append("<tr><td>" + broadcastMessage + "</td></tr>");
}

/**
 * Appends a private message to the #privateMessages table.
 * If the logged-in user is the sender, label it as "Me".
 *
 * @param {string} loggedInUser - The username of the logged-in user.
 * @param {string} from - The sender of the message.
 * @param {string} to - The recipient of the message.
 * @param {string} message - The message text.
 */
function showMessage(loggedInUser, from, to, message) {
    // Basic input validation
    if (typeof loggedInUser !== "string" || typeof from !== "string" ||
        typeof to !== "string" || typeof message !== "string") {
        console.error("Invalid input types for showMessage");
        return;
    }

    // Determine display name for sender
    const senderDisplay = (loggedInUser.trim().toLowerCase() === from.trim().toLowerCase())
        ? "Me"
        : from;

    // Escape HTML to prevent XSS
    const escapeHTML = (str) => str
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

    // Append formatted row to the table
    $("#privateMessages").append(
        `<tr>
            <td><strong>${escapeHTML(senderDisplay)}</strong> → <em>${escapeHTML(to)}</em></td>
            <td>${escapeHTML(message)}</td>
        </tr>`
    );
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#registerSession" ).click(() => sendName());
    $( "#sendToUser" ).click(() => sendToUser());
});

