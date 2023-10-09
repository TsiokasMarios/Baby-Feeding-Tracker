<script>
    import { createEventDispatcher } from 'svelte';


    export let feedingInstance;
    export let showModal;
    export let onSave;

    const dispatch = createEventDispatcher();
  
    function handleSave() {
      onSave();
    }

    function close() {
      dispatch('cancel');
    } 
  </script>
  
  {#if showModal}
    <div class="backdrop">
      <div class="modal">
        <h2>Edit Feeding Instance</h2>
        <form on:submit|preventDefault={handleSave}>
          <label>
            Amount: 
            <input type="number" step="0.1" bind:value={feedingInstance.amountMl} > 
          </label>
          <br />
          <label>
            Start time:
            <input type="datetime-local" bind:value={feedingInstance.startTime} />
          </label>
          <br />
          <label>
            End time:
            <input type="datetime-local" bind:value={feedingInstance.endTime} />
          </label>
          <br />
          <button on:click|preventDefault={close}>Cancel</button>
          <button type="submit">Edit</button>
        </form>
      </div>
    </div>
  {/if}
  
  <style>
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
  