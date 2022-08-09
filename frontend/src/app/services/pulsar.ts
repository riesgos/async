import { Observable } from "rxjs";


export class Consumer {

    private ws: WebSocket;

    constructor(url: string) {
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

    private ws: WebSocket;

    constructor(url: string) {
        this.ws = new WebSocket(url);
    }

    public postMessage(body: string, properties?: { [key: string]: string }, context?: string): Observable<Response> {
        return new Observable(subscriber => {
            const message: Message = {
                payload: btoa(body),
                properties,
                context
            }
            this.ws.onmessage = function (response: MessageEvent<string>) {
                const data: Response = JSON.parse(response.data);
                if (data.errorCode !== 0) {
                    subscriber.error(data);
                } else {
                    subscriber.next(data);
                }
                subscriber.complete();
            }
            this.ws.send(JSON.stringify(message));
        });
    }

    public close() {
        this.ws.close();
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
