import { Schedule } from "./Schedule";
import { SchedulerEvent } from "./SchedulerEvent";

type Methods = 'GET' | 'POST' | 'PUT' | 'DELETE';
export type HTTPPayload = { url: string; method: Methods; body?: any };

export class HttpEvent extends SchedulerEvent<HTTPPayload> {
    constructor(
        id: number = 0,
        private url: string,
        private method: Methods,
        private body?: any,
        schedule?: Schedule | undefined
    ) {
        super(id, 'http', schedule);
    }
    
    getPayload(): HTTPPayload {
        return {
            url: this.url,
            method: this.method,
            body: this.body
        }
    }
}