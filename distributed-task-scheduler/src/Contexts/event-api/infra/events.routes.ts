import { Router, Express, Request, Response } from "express";

export class EventRoutes {
    private router: Router = Router();
    constructor(
        private app: Express
    ) {
        this.initRoutes();
    }

    private initRoutes() {
        this.router.get('/schedule', (req: Request, res: Response) => {
            res.status(200).json({ "hello": "world" });
        });

        this.app.use('/events', this.router);
    }
}