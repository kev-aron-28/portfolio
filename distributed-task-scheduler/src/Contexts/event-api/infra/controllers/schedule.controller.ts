import { NextFunction, Request, Response } from "express";
import { EventPayloadMap, EventType, SchedulerEvent } from "../../domain/SchedulerEvent";
import { EventFactory } from "../../domain/EventFactory";
import { Schedule } from "../../domain/Schedule";
import { ScheduleUseCase } from "../../application/Schedule.usecase";

interface CreateEventBody<T extends keyof EventPayloadMap> {
    type: T;
    payload: EventPayloadMap[T];
    schedule?: Schedule;
}

export class ScheduleController {
    constructor(
        private scheduleUseCase: ScheduleUseCase
    ) {}

    // All types of payload
    // For HTTP event url, method, body
    // For Email event subject, to, body
    // For 

    public async run(req: Request, res: Response, next: NextFunction): Promise<void> {
        try {
            const { type, payload, schedule } = req.body as CreateEventBody<keyof EventPayloadMap>;

            if(!type || !payload) {
                throw { message: 'Missing parameters in body', status: 400 };
            }

            const event: SchedulerEvent<any> = await this.scheduleUseCase.run(
                type,
                payload,
                schedule
            );

            res.status(200).json(event.toPrimitives());
        } catch (error) {
            next(error);
        }
        
    }
}