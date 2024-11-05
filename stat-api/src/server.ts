import { readFileSync, writeFileSync } from "node:fs";

const minimax: any[] = [];
let is_minimax_unsaved = false;

// load the JSON from minimax.json
let json = readFileSync("src/database/minimax.json", "utf8");
JSON.parse(json).forEach((d: any) => minimax.push(d));

function cron_job(){
    setInterval(
        function(){
            if (is_minimax_unsaved)
                writeFileSync("src/database/minimax.json", JSON.stringify(minimax));
        },
        10 * 60 * 1000, // 10m
    )
}


const server = Bun.serve({
    port: 8080,
    async fetch(req) {
        if (req.method !== "POST") {
            return Response.json(
                {message: "The method 'GET' is not expected"},
                {status: 400}
            );
        }

        let [_, path] = req.url.split(/:[0-9]+\//);

        if (path.startsWith("minimax")){
            let data: object = await req.json() as object;

            minimax.push(data)

            if (!is_minimax_unsaved)
                is_minimax_unsaved = true

            return Response.json(
                {message:"Entry added"},
                {status: 200}
            )
        }

        return Response.json(
            {message:"The entry your provided is unknown",allowedEntries:["minimax"]},
            {status: 404}
        );
    },
});

cron_job();

console.log(`Listening on ${server.url}`);