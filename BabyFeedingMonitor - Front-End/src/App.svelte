<script lang="ts">
	import ItemList from "./components/ItemList.svelte";
	import DateTimePicker from "./components/dateTimePicker.svelte";
	import AvgFeedingSessionDuration from "./components/avgFeedingSessionDuration.svelte";
	import AvgMilkConsumed from "./components/avgMilkConsumed.svelte";
	import Login from "./components/login.svelte";
	import CreateInstance from "./components/createInstance.svelte";
	import Charts from "./components/charts.svelte";
	import { authStore } from "./store";
	import { onMount } from "svelte";

	let baseURI = "http://localhost:8081/BabyFeedingTracker/feeding";
	let showDatePicker = false;
	let showAvgFeedingDurationModal = false;
	let feedingInstances = [];
	let avgDuration;
	let searchingForAvgMilk = false;
	let showMilkModal = false;
	let avgMilk;

	let loggedIn = false;
	let authString = "";

	onMount(() => {
		const unsubscribe = authStore.subscribe((value) => {
			authString = value;
		});

		return unsubscribe;
	});

	const onLogin = (value) => {
		loggedIn = value;
	};

	const toggleDatePicker = () => {
		showDatePicker = !showDatePicker;
	};

	const toggleModal = () => {
		showAvgFeedingDurationModal = !showAvgFeedingDurationModal;
	};

	const handleDatePickerSubmit = (event) => {
		feedingInstances = event.detail;
		console.log(event);
	};

	const getAvgFeedingDuration = async () => {
		const response = await fetch(
			`${baseURI}/feeding/average-feeding-duration`,
			{
				headers: {
					Authorization: "Basic "+ authString,
				},
			}
		);
		avgDuration = await response.json();
		console.log(response);

		showAvgFeedingDurationModal = true;
	};

	const showAvgFeedingDuration = () => {
		toggleModal();
		getAvgFeedingDuration();
	};

	const toggleAvgMilkConsumed = () => {
		searchingForAvgMilk = !searchingForAvgMilk;
	};

	const handleMilkConsumed = (event) => {
		searchingForAvgMilk = !searchingForAvgMilk;
		showMilkModal = true;
		avgMilk = event.detail;
	};

	const toggleMilkConsumed = () => {
		showMilkModal = !showMilkModal;
	};
</script>

{#if !loggedIn}
	<Login {onLogin} />
{/if}

<DateTimePicker
	{showDatePicker}
	on:submit={handleDatePickerSubmit}
	on:click={toggleDatePicker}
/>
<AvgFeedingSessionDuration
	{showAvgFeedingDurationModal}
	{avgDuration}
	on:click={toggleModal}
/>
<AvgMilkConsumed
	{searchingForAvgMilk}
	on:click={toggleAvgMilkConsumed}
	on:submit={handleMilkConsumed}
/>

{#if showMilkModal}
	<!-- svelte-ignore a11y-click-events-have-key-events -->
	<div class="milk-backdrop" on:click|self={toggleMilkConsumed}>
		<div class="milk-consumed-modal modal">
			<h1>Average milk consumed</h1>
			<h2>{avgMilk}</h2>
		</div>
	</div>
{/if}

{#if loggedIn}
	<main>
		<h1 class="title">Baby Feeding Tracker</h1>
		<CreateInstance />
		<h2>Filter by</h2>
		<button on:click={toggleDatePicker}>Date</button>
		<button on:click={toggleAvgMilkConsumed}>Average milk consumed</button>
		<button on:click={showAvgFeedingDuration}
			>Average feeding session</button
		>

		<ItemList {feedingInstances} />
		<h2>Graph</h2>
		<Charts />
	</main>
{/if}

<style>
	main {
		text-align: center;
		/* padding: 1em; */
		max-width: 240px;
		margin: 0 auto;
	}

	.title {
		color: #ff3e00;
		text-transform: uppercase;
		font-size: 4em;
		font-weight: 100;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}

	div {
		font-size: medium;
		align-items: center;
		width: auto;
		text-align: center;
		position: relative;
	}

	.milk-backdrop {
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
