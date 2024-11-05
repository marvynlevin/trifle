import {readFileSync, writeFileSync} from "node:fs"
import {formatDuration} from "./utils.ts";

const MAX_DEPTH = 50;

let json: MinMaxStat[]  = JSON.parse(
    readFileSync("minimax.json", "utf-8")
);

// Le temps pris par chaque calcul de poids
let allWeightComputationTime: number[] = [];

// Le temps pris pour construire l'arbre
let allBuildTime: number[] = [];

// Le temps pris à chaque fois pour trouver le meilleur noeud
let allPathFinderTime: number[] = [];

type LayoutDict = {
    [key: number]: number
};

let layouts: LayoutDict[] = [];

interface MinMaxStat {
    layouts: LayoutDict,
    weightComputationTime: number[],
    buildTreeStart: number,
    buildTreeEnd: number,
    findPathStart: number,
    findPathEnd: number,
}

class FinalStats {
    samples:                        number     = 0;
    median_weight_computation_time: number     = 0;
    median_tree_building_time:      number     = 0;
    median_path_finding_time:       number     = 0;

    // Nombre moyen de nœuds par layout
    layouts_dicts:                  LayoutDict = {};
    // Temps de construction de l'arbre basé sur le nombre de nœuds
    build_tree_time_based_on_nodes: LayoutDict = {};
    // Temps de recherche du chemin basé sur le nombre de nœuds
    find_path_time_based_on_nodes:  LayoutDict = {};
}

let final_stats: FinalStats = new FinalStats();

// Extract information from the JSON:D
json.forEach(function(entry: MinMaxStat){
    entry.weightComputationTime.map(w => allWeightComputationTime.push(w));

    let build_tree_time = entry.buildTreeEnd - entry.buildTreeStart;
    allBuildTime.push(build_tree_time);
    let find_path_time = entry.findPathEnd - entry.findPathStart;
    allPathFinderTime.push(find_path_time);

    layouts.push(entry.layouts);

    let number_of_nodes = 0;
    for (let k of Object.keys(entry.layouts)) {
        let key = parseInt(k);
        number_of_nodes += entry.layouts[key];
    }

    if (!(number_of_nodes in final_stats.build_tree_time_based_on_nodes))
        final_stats.build_tree_time_based_on_nodes[number_of_nodes] = 0;
    final_stats.build_tree_time_based_on_nodes[number_of_nodes] += build_tree_time;

    if (!(number_of_nodes in final_stats.find_path_time_based_on_nodes))
        final_stats.find_path_time_based_on_nodes[number_of_nodes] = 0;
    final_stats.find_path_time_based_on_nodes[number_of_nodes] += find_path_time;
});

function extract_stats(){
    final_stats.samples = json.length;
    console.log("  Number of samples:         " + final_stats.samples);
    console.log()

    // it's extract times :D
    final_stats["median_weight_computation_time"] = allWeightComputationTime
        .reduce((a,b) => a + b, 0) / allWeightComputationTime.length;
    console.log("  Median weight time:        " + formatDuration(final_stats["median_weight_computation_time"]))

    final_stats["median_tree_building_time"] = allBuildTime
        .reduce((a,b) => a + b, 0) / allBuildTime.length;
    console.log("  Median tree building time: " + formatDuration(final_stats["median_tree_building_time"]))

    final_stats["median_path_finding_time"] = allPathFinderTime
        .reduce((a,b) => a + b, 0) / allPathFinderTime.length;
    console.log("  Median path finding time:  " + formatDuration(final_stats["median_path_finding_time"]))


    layouts.forEach(function(layout){
        Object.keys(layout)
            .forEach(function(k){
                let key = MAX_DEPTH - parseInt(k);

                if (!(key in final_stats.layouts_dicts))
                    final_stats.layouts_dicts[key] = 0;

                final_stats.layouts_dicts[key] += layout[parseInt(k)];
            })
    });

    Object.keys(final_stats.layouts_dicts)
        .forEach(function(k){
            let key = parseInt(k);

            final_stats.layouts_dicts[key] /= layouts.length;
        })

    console.log("\n  Median number of nodes per layout: ");
    for (let key of Object.keys(final_stats.layouts_dicts)){
        let k = parseInt(key);
        console.log(`    ${k}: ${final_stats.layouts_dicts[k]}`);
    }
    console.log()

    // apply the division to the build_tree_time_based_on_nodes and find_path_time_based_on_nodes
    for (let k of Object.keys(final_stats.build_tree_time_based_on_nodes)) {
        let key = parseInt(k);
        final_stats.build_tree_time_based_on_nodes[key] /= json.length;
        final_stats.find_path_time_based_on_nodes[key] /= json.length;
    }

    console.log("  Median time for building tree (based on nodes number):")
    for (let key of Object.keys(final_stats.build_tree_time_based_on_nodes)){
        let k = parseInt(key);
        console.log(`    ${k}: ${formatDuration(final_stats.build_tree_time_based_on_nodes[k])}`);
    }
    console.log()
    console.log("  Median time for finding path (based on nodes number):")
    for (let key of Object.keys(final_stats.find_path_time_based_on_nodes)){
        let k = parseInt(key);
        console.log(`    ${k}: ${formatDuration(final_stats.find_path_time_based_on_nodes[k])}`);
    }
}

const arrayToTypst = (...args: any[]) : string => `(${args.join(',')})`;
const nanoToMs = (nanoseconds: number) => {
    const milliseconds = Math.floor((nanoseconds % (1_000_000_000)) / 1_000_000);
    const remainingNanos = nanoseconds % 1_000_000;

    return `${milliseconds}${remainingNanos > 0 ? '.' + Math.floor(remainingNanos) : ""}`;
};

function format_to_typst(){
    let f = "";

    f += `#let minimax_median_tree_building_time = ${nanoToMs(final_stats.median_tree_building_time)};`;
    f += "\n";

    f += `#let minimax_median_path_finding_time = ${nanoToMs(final_stats.median_path_finding_time)};`;
    f += "\n";

    f += `#let minimax_median_weight_computation_time = ${nanoToMs(final_stats.median_weight_computation_time)};`;
    f += "\n";

    f += `#let minimax_samples = ${final_stats.samples};`
    f += "\n\n";

    f += "#let minimax_layouts = ";
    f += arrayToTypst(
        [...Object.entries(final_stats.layouts_dicts)]
            .map(([k, v]) => arrayToTypst(v, k)),
    );
    f += ";\n\n"

    f += "#let minimax_build_tree_time_based_on_nodes_no = ";
    f += arrayToTypst(
        [...Object.entries(final_stats.build_tree_time_based_on_nodes)]
            .map(([k, v]) => arrayToTypst(k, nanoToMs(v))),
    );
    f += ";\n\n"


    f += "#let minimax_finding_path_time_based_on_nodes_no = ";
    f += arrayToTypst(
        [...Object.entries(final_stats.find_path_time_based_on_nodes)]
            .map(([k, v]) => arrayToTypst(k, nanoToMs(v))),
    );
    f += ";\n\n"


    writeFileSync("minimax_stats.typ", f, "utf8");
}


console.log("Extracting statistics...\n");
extract_stats();
console.log("\nStatistics extracted");

console.log("Exporting to minimax_stats.typ...");
format_to_typst();
console.log("Finished exporting the statistics to a typst file.");
