import pool from './postgres.config.ts';
import { EventRepository } from "../domain/EventRepository.ts";
import { SchedulerEvent } from "../domain/SchedulerEvent.ts";

export class PostgresRepository implements EventRepository {
    async schedule(event: SchedulerEvent<any>): Promise<void> {
        const query = `
            INSERT INTO events (type, schedule, payload, nextrun)
            VALUES ($1, $2, $3, $4)
        `;

        const rawEvent = event.toPrimitives();
        
        const nextRun = event.calculateNextRun();

        const valuesToInsert = [
            rawEvent.type,
            rawEvent.schedule ? JSON.stringify(rawEvent.schedule) : null,
            JSON.stringify(event.getPayload()),
            nextRun
        ];

        try {
            await pool.query(query, valuesToInsert);
        } catch (error) {
            console.log('ERROR INSERT EVENT: ' + error);
            throw new Error('Error when trying to insert to event');
        }
    }
}