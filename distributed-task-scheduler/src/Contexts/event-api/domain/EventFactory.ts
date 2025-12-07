import { EmailEvent } from "./EmailEvent";
import { HttpEvent } from "./HttpEvent";
import { Schedule } from "./Schedule";
import { SchedulerEvent } from "./SchedulerEvent";

export class EventFactory {
    static create(type: string, payload: any, schedule?: Schedule): SchedulerEvent<any> {
        switch(type) {
            case 'http':
                return new HttpEvent(0, payload.url, payload.method, payload.body, schedule);
            case 'email':
                return new EmailEvent(0, payload.subject, payload.to, payload.body, schedule);
            default:
                throw new Error('Unsupported event type ' + type);
        }
    }
}