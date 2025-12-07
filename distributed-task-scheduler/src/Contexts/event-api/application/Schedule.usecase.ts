import { EventFactory } from "../domain/EventFactory";
import { EventRepository } from "../domain/EventRepository";
import { Schedule } from "../domain/Schedule";
import { EmailPayload, HttpPayload, InternalPayload, SchedulerEvent } from "../domain/SchedulerEvent";

export class ScheduleUseCase {
    constructor(
        private repository: EventRepository
    ) {}

    public async run(
        type: string, 
        payload: HttpPayload | EmailPayload | InternalPayload, 
        schedule: Schedule | undefined 
    ): Promise<SchedulerEvent<any>> {
        
        const event: SchedulerEvent<any> = EventFactory.create(
            type,
            payload,
            schedule
        );

        await this.repository.schedule(event);

        return event;
    }
}