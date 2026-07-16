import { Schedule } from "./Schedule";
import { SchedulerEvent } from "./SchedulerEvent";

export type EmailPayload = { subject: string; to: string; body: string; };

export class EmailEvent extends SchedulerEvent<EmailPayload> {
    constructor(
        id: number = 0,
        private subject: string,
        private to: string,
        private body: string,
        schedule?: Schedule | undefined
    ) {
        super(id, 'email', schedule);
    }

    getPayload(): EmailPayload {
        return {
            body: this.body,
            to: this.to,
            subject: this.subject
        }
    }
}