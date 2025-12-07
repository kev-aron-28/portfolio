import { Router, Express, Request, Response, NextFunction } from "express";
import { ScheduleController } from "./controllers/schedule.controller";
import { ScheduleUseCase } from "../application/Schedule.usecase";
import { EventRepository } from "../domain/EventRepository";
import { PostgresRepository } from "./postgres.repository";

export class EventRoutes {
    private router: Router = Router();
    constructor(
        private app: Express
    ) {
        this.initRoutes();
    }

    private initRoutes() {
        const eventRepository: EventRepository = new PostgresRepository();

        const scheduleUseCase: ScheduleUseCase = new ScheduleUseCase(eventRepository);
        
        const scheduleController: ScheduleController = new ScheduleController(
            scheduleUseCase
        );
        
        this.router.post('/schedule', (req: Request, res: Response, next: NextFunction) => {
            scheduleController.run(req, res, next);
        });

        this.app.use('/events', this.router);
    }
}