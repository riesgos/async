import { Observable, map, switchMap } from "rxjs";


export class Consumer {

    private ws: WebSocket;

    constructor(readonly url: string) {
        this.ws = new WebSocket(url);
    }

    public readMessages(): Observable<any> {
        return new Observable(subscriber => {
            this.ws.onmessage = function (response: MessageEvent<string>) {
                const data: Response = JSON.parse(response.data);
                const messageId = data.messageId;
                this.send(JSON.stringify({ messageId }));  // ACK
                if (data.errorCode !== 0) {
                    subscriber.error(data);
                } else {
                    subscriber.next(data);
                }
            }
        });
    }

    public close() {
        this.ws.close();
    }
}


export class Producer {

    constructor(private url: string) {}

    connect(): Observable<WebSocket> {
        console.log(`Producer: connecting to ${this.url}...`);
        return new Observable(subscriber => {
            const ws = new WebSocket(this.url);
            ws.onopen = function (ev: Event) {
                console.log(`... producer: connected successfully to ${this.url}.`);
                subscriber.next(ws);
                subscriber.complete();
            };
            ws.onerror = function (ev: Event) {
                subscriber.error(ev);
                subscriber.complete();
            }
        });
    }


    public postMessage(body: string, properties?: { [key: string]: string }, context?: string): Observable<Response> {
        const ws$ = this.connect();
        return ws$.pipe(
            switchMap(ws => this.makePost(ws, body, properties, context))
        );
    }

    private makePost(ws: WebSocket, body: string, properties?: { [key: string]: string }, context?: string) {
        const post$ = new Observable<Response>(subscriber => {
            if (!ws || !ws.OPEN) {
                subscriber.error(`Not connected`);
                subscriber.complete();
            }

            // 1. create message
            const message: Message = {
                payload: btoa(body),
                properties,
                context
            }

            // 3. react to responses
            ws.onmessage = function (response: MessageEvent<string>) {
                const data: Response = JSON.parse(response.data);
                console.log("WS got data:", data)
                if (data.errorCode !== 0) {
                    subscriber.error(data);
                } else {
                    subscriber.next(data);
                }
                subscriber.complete();
            }

            // 2. send message
            console.log(`Sending message through websocket: `, body, message);
            ws.send(JSON.stringify(message));
        });
        return post$;
    }

}

type Protocol = 'ws' | 'wss';
type ClientType = 'producer' | 'consumer';
type StorageType = 'persistent' | 'non-persistent';

export interface Response {
    result: 'ok' | string,
    messageId: string,
    errorCode: number,
    schemaVersion: number
}

export interface Message {
    payload: string,
    properties?: {
        [key: string]: string
    },
    context?: string // fortlaufende nummer
}
