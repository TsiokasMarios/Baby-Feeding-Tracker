<script>
    import { createEventDispatcher, onMount } from "svelte";
    import { authStore } from "../store";

    const dispatch = createEventDispatcher();
    let authString;

    onMount(() => {
		const unsubscribe = authStore.subscribe((value) => {
			authString = value;
		});

		return unsubscribe;
	});

    export let searchingForAvgMilk = false;
    let avgMilk = -1;
    let showMilkModal = false;

    let baseURI = "http://localhost:8081/BabyFeedingTracker/feeding";

    const handleSubmit = async (event) => {
        let startTime = event.target.querySelector(".start-time").value;
        let endTime = event.target.querySelector(".end-time").value;
        if (startTime == "" && endTime == "") {
            const response = await fetch(`${baseURI}/feeding/average-milk-consumed`, {
                headers: {
                    Authorization: "Basic " + authString,
                    "Content-Type": "application/json",
                },
            });
            avgMilk = await response.json();
        } else {
            let start = new Date(startTime).toISOString();
            let end = new Date(endTime).toISOString();
            start = start.slice(0, 19).replace("T", " ");
            end = end.slice(0, 19).replace("T", " ");
            const response = await fetch(
                `${baseURI}/feeding/average-milk-consumed?start=${start}&end=${end}`,
                {
                    headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                }
                }
            );
            avgMilk = await response.json();
        }

        if (avgMilk != -1) {
            searchingForAvgMilk = false;
            showMilkModal = true;
        }
        dispatch("submit", avgMilk);
    };
</script>

{#if searchingForAvgMilk}
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="backdrop" on:click|self>
        <div class="modal">
            <h2>Pick dates to search for</h2>
            <form on:submit|preventDefault={handleSubmit}>
                <label>
                    Start time:
                    <input class="start-time" type="datetime-local" />
                </label>
                <br />
                <label>
                    End time:
                    <input class="end-time" type="datetime-local" />
                </label>
                <br />
                <button type="submit">Search</button>
            </form>
        </div>
    </div>
{/if}

<style>
    div {
        font-size: medium;
        align-items: center;
        width: auto;
        text-align: center;
        position: relative;
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
