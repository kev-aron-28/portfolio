import { SchedulerEvent } from "./SchedulerEvent";

export interface EventRepository {
    schedule(event: SchedulerEvent<any>): Promise<void>
}