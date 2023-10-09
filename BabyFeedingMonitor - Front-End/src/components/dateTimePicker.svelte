<script>
    import { createEventDispatcher } from "svelte";
    import {authStore} from '../store';
    import { onMount } from "svelte";

    let authString;

    onMount(() => {
		// Subscribe to the authStore to get updates to the auth string
		const unsubscribe = authStore.subscribe((value) => {
			authString = value;
		});

		// Unsubscribe from the store when the component is destroyed
		return unsubscribe;
	});

    export let showDatePicker = false;

    let baseURI = "http://localhost:8081/BabyFeedingTracker/feeding/feeding";

    const dispatch = createEventDispatcher();

    const handleSubmit = async (event) => {
        console.log(authString);
        let start = new Date(
            event.target.querySelector(".start-time").value
        ).toISOString();
        let end = new Date(
            event.target.querySelector(".end-time").value
        ).toISOString();
        start = start.slice(0, 19).replace("T", " ");
        end = end.slice(0, 19).replace("T", " ");

        const response = await fetch(`${baseURI}?start=${start}&end=${end}`, {
            headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                }
        });
        const jsonData = await response.json();

        dispatch("submit", jsonData);
        showDatePicker = false;
    };
</script>

{#if showDatePicker}
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
