<script>
    import { onMount } from "svelte";
    import { Chart, registerables } from "chart.js"; // Import the specific modules
    import {authStore} from '../store';


    let authString = '';

    onMount(() => {
		// Subscribe to the authStore to get updates to the auth string
		const unsubscribe = authStore.subscribe((value) => {
			authString = value;
		});

		// Unsubscribe from the store when the component is destroyed
		return unsubscribe;
	});

    // Register the necessary controllers
    Chart.register(...registerables);


    let showDatePicker2 = false;

    let chart;
    let chartData = {
        labels: [],
        datasets: [],
    };
    let chartOptions = {};

    let uri = "http://localhost:8081/BabyFeedingTracker/feeding/feeding/charts"

    async function fetchChartData(uri) {//Get the chart data
        const response = await fetch(
            uri,
            {
                headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                }
            }
        );
        const jsonData = await response.json();
        chartData = jsonData;
        updateChart();
    }

    function initializeChart() {
        const ctx = document.getElementById("chart").getContext("2d");
        chart = new Chart(ctx, {
            type: "line",
            data: chartData,
            options: chartOptions,
        });
    }

    function updateChart() {
        if (chart) {
            chart.data = chartData;
            chart.update();
        }
    }

    function handleShow(){
        showDatePicker2 = !showDatePicker2;
    }

    const handleSubmit = async (event) => {
        let start = new Date(event.target.querySelector(".start-time").value).toISOString();
        let end = new Date(event.target.querySelector(".end-time").value).toISOString();
        start = start.slice(0, 19).replace('T', ' ');
        end = end.slice(0, 19).replace('T', ' ');

        fetchChartData(`${uri}?start=${start}&end=${end}`)
        showDatePicker2 = false;
    };

    onMount(() => {//Runs as soon as the component is loaded
        initializeChart();
        fetchChartData(uri);
    });
</script>

<button on:click={handleShow}>Filter by date</button>

<div class="centered">
    <div class="chart-container">
        <canvas id="chart" width="400" height="400" />
    </div>
</div>


{#if showDatePicker2}
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="backdrop" on:click|self>
        <div class="modal">
            <h2>Pick feeding session start and end time</h2>
            <form on:submit|preventDefault={handleSubmit}>
                <label>
                    Start time:
                    <input class="start-time" type="datetime-local" />
                </label>
                <br />
                <label>
                    End time:
                    <input class="end-time" type="datetime-local"/>
                </label>
                <br />
                <button on:click={handleShow}>Cancel</button>
                <button type="submit">Search</button>
            </form>
        </div>
    </div>
{/if}

<style>
    .chart-container {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 50%;
      margin: 0 auto;
    }
    
    #chart {
      width: 80%;
      height:auto;
    }

    .backdrop {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 9999;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .modal {
        background-color: whitesmoke;
    }
</style>
