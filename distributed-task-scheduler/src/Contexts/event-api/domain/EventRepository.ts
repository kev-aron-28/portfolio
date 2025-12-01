import { SchedulerEvent } from "./SchedulerEvent.ts";

export interface EventRepository {
    schedule(event: SchedulerEvent<any>): Promise<void>
}