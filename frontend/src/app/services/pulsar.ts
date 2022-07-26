import { Observable } from "rxjs";


export class Producer {

    private ws: WebSocket;

    constructor(
        protocol: Protocol = "ws",
        domain: string = "localhost",
        port: number = 8080,
        storageType: StorageType = "non-persistent",
        tenant = "public",
        clusterRegion = "standalone",
        namespace = "default",
        topic: string = "riesgos") {

        // @TODO: producer.accessmode.shared
        // @TODO: jwt-tokens
        // @TODO: log-level
        // @TODO: consumer, auto-ack
        const clientType: ClientType = "producer";

        this.ws = new WebSocket(`${protocol}://${domain}:${port}/ws/v2/${clientType}/${storageType}/${tenant}/${clusterRegion}/${namespace}/${topic}`);
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
    result: 'ok',
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
