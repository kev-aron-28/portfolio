import { Schedule } from "./Schedule.ts";
import { SchedulerEvent } from "./SchedulerEvent.ts";

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