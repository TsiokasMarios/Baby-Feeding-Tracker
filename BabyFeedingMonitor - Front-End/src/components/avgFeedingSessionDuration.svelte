<script>
    export let showAvgFeedingDurationModal = false;
    export let avgDuration = "Loading";

</script>

{#if showAvgFeedingDurationModal}
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="backdrop" on:click|self>
        <div class="modal">
            <h1>Average feeding session duration</h1>
            <h2>{avgDuration}</h2>
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
