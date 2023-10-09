<script>
    import { createEventDispatcher } from "svelte";
    import { onMount } from "svelte";
    import { authStore } from "../store";
    import { prevent_default } from "svelte/internal";

    let milkConsumed;
    let startTime;
    let endTime;
    let authString;

    const dispatch = createEventDispatcher();

    onMount(() => {
        // Subscribe to the authStore to get updates to the auth string
        const unsubscribe = authStore.subscribe((value) => {
            authString = value;
        });

        // Unsubscribe from the store when the component is destroyed
        return unsubscribe;
    });

    async function submit() {//Create feeding instance
        const url = "http://localhost:8081/BabyFeedingTracker/feeding/feeding";
        let start = new Date(startTime).toISOString().slice(0, -5);
        let end = new Date(endTime).toISOString().slice(0, -5);

        const data = {
            amountMl: milkConsumed,
            startTime: start,
            endTime: end,
        };

        console.log(data);

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: "Basic " + authString,
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                // Handle successful response
                console.log("Created successfully");
                dispatch("submit");
            } else if (response.status == 403) {
                // Handle authorization error response
                alert("You do not permission to perform this action")
            }
        } catch (error) {
            console.error("Error:", error);
        }
    }
</script>

<div class="createInstanceDiv">
    <form on:submit|preventDefault={submit}>
        <div class="form-row">
            <label>
                Amount of milk consumed:
                <input bind:value={milkConsumed} type="number" />
            </label>

            <label>
                Feeding start time:
                <input
                    bind:value={startTime}
                    type="datetime-local"
                    class="start-time"
                />
            </label>

            <label>
                Feeding start time:
                <input
                    bind:value={endTime}
                    type="datetime-local"
                    class="end-time"
                />
            </label>

            <button type="submit">Create</button>
        </div>
    </form>
</div>

<style>
    .createInstanceDiv {
        display: flex;
        justify-content: center;
        text-align: center;
        /* padding: 1em; */
        /* max-width: 240px; */
        margin: 0 auto;
    }

    .form-row {
        display: flex;
        align-items: center;
        gap: 10px;
    }
</style>
