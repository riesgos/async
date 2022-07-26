import { WebSocketServer } from 'ws';

/** https://github.com/websockets/ws */
const wss = new WebSocketServer({ port: 8080 });

wss.on('connection', function connection(ws) {
    ws.on('message', function message(data) {
        console.log('received: %s', data);
        ws.send(`you send me: %s ${data}`);
    });

    ws.send('something');
});