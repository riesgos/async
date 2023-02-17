import { Observable } from "rxjs";


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

    private ws?: WebSocket;

    connect(url: string): Observable<boolean> {
        return new Observable(subscriber => {
            this.ws = new WebSocket(url);
            this.ws.onopen = function (ev: Event) {
                subscriber.next(true);
                subscriber.complete();
            };
            this.ws.onerror = function (ev: Event) {
                subscriber.error(ev);
                subscriber.complete();
            }
        });
    }

    public isConnected(): boolean {
        return !!(this.ws) && !!(this.ws.OPEN);
    }

    public postMessage(body: string, properties?: { [key: string]: string }, context?: string): Observable<Response> {
        return new Observable(subscriber => {
            if (!this.isConnected()) {
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
            this.ws!.onmessage = function (response: MessageEvent<string>) {
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
            this.ws!.send(JSON.stringify(message));
        });
    }

    public close() {
        this.ws?.close();
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
