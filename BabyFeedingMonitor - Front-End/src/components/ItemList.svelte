<script>
    import { onMount } from "svelte";
    import DateTimePicker from "./dateTimePicker.svelte";
    import { createEventDispatcher } from "svelte";
    import EditModal from "./editModal.svelte";
    import {authStore} from '../store';


    let authString;

    onMount(() => {
		// Subscribe to the authStore to get updates to the auth string
		const unsubscribe = authStore.subscribe((value) => {
			authString = value;
		});

		// Unsubscribe from the store when the component is destroyed
		return unsubscribe;
	});

    export let feedingInstances = [];

    let selectedFeedingInstance;

    let showModal = false;

    let baseURI = "http://localhost:8081/BabyFeedingTracker/feeding/";

    onMount(async () => { 
        //Get all feeding instances
        const response = await fetch(baseURI + "feeding", {
            headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                }
        });
        feedingInstances = await response.json();
        console.log(feedingInstances);
    });

    async function deleteRow(feedingInstance) {//Delete feeding instance
        const response = await fetch(
            baseURI + "feeding/" + feedingInstance.id,
            {
                method: "DELETE",
                headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                }
            }
        );

        if (response.ok) {
            feedingInstances = feedingInstances.filter(
                (instance) => instance.id !== feedingInstance.id
            );
        } else if (response.status == 403) {
                // Handle authorization error response
                window.alert("You do not permission to perform this action")
            }
    }

    function handleEdit(feedingInstance) {
        showModal = true;
        selectedFeedingInstance = feedingInstance;
    }

    async function onSave() { //Edit feeding instance
        const response = await fetch(
            baseURI + `feeding/${selectedFeedingInstance.id}`,
            {
                method: "PUT",
                headers:{
                    'Authorization': 'Basic ' + authString,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(selectedFeedingInstance),
            }
        );

        if (response.ok) {
            const index = feedingInstances.findIndex(
                (item) => item.id === selectedFeedingInstance.id
            );
            if (index !== -1) {
                feedingInstances[index] = selectedFeedingInstance;
            }
        }else if (response.status == 403) {
                alert("You do not permission to perform this action")
            }

        showModal = false;
        selectedFeedingInstance = null;
    }

    function handleCancel() {
        showModal = false;
    }
</script>

<EditModal
    feedingInstance={selectedFeedingInstance}
    {showModal}
    {onSave}
    on:cancel={handleCancel}
/>

<div class="centered">
    <div class="container">
        <table>
            <thead>
                <th>Amount</th>
                <th>Start time</th>
                <th>End time</th>
            </thead>
            {#each feedingInstances as feedingInstance (feedingInstance.id)}
                <tr>
                    <td class="amount"> Amount {feedingInstance.amountMl}</td>
                    <td class="date">
                        {new Date(feedingInstance.startTime).toLocaleString(
                            "en-US",
                            {
                                hour: "2-digit",
                                minute: "2-digit",
                                day: "2-digit",
                                month: "2-digit",
                                year: "numeric",
                            }
                        )}
                    </td>
                    <td class="date"
                        >{new Date(feedingInstance.endTime).toLocaleString(
                            "en-US",
                            {
                                hour: "2-digit",
                                minute: "2-digit",
                                day: "2-digit",
                                month: "2-digit",
                                year: "numeric",
                            }
                        )}</td
                    >
                    <td class="actions">
                        <button on:click|preventDefault={() => handleEdit(feedingInstance)}
                            >Edit</button
                        >
                        <button on:click|preventDefault={() => deleteRow(feedingInstance)}
                            >Delete</button
                        >
                    </td>
                </tr>
            {/each}
        </table>
    </div>
</div>

<style>
    div {
        font-size: medium;
        align-items: center;
    }

    table {
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: auto;
        text-align: center;
    }
    td,
    th {
        border: 1px solid #dddddd;
        text-align: center;
        padding: 8px;
    }

    .amount {
        min-width: 125px;
    }
    .date {
        width: 250px;
    }

    .centered {
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>
