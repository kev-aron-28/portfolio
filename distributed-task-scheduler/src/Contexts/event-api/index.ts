import express, { Request, Response, Router } from 'express';
import { EventRoutes } from './infra/events.routes';
import { errorHandler } from './infra/controllers/errorHandler';

const PORT = 3000;

const app = express();
app.use(express.json());

new EventRoutes(app);

app.get('/test', (req: Request, res: Response) => {
    res.status(200).send('Hello world');
});

app.use(errorHandler);

app.listen(PORT, () => {
    console.log(`App running on http://localhost:${PORT}/`);
}).on("error", (error: any) => {
    throw new Error(error.message);
});