<template>
  <v-snackbar
    top
    v-model="snackbar"
    color="error"
  >
    {{ message }}
    <v-btn darl flat @click.native="snackbar = false">{{ i18n.btn_close }}</v-btn>
  </v-snackbar>
</template>

<script>
export default {
  name: 'AppErrorToast',
  props: ['message', 'showErrorDialog'],
  data () {
    return {
      snackbar: this.showErrorDialog,
      i18n: {
        'btn_close': 'Close'
      }
    }
  },
  watch: {
    showErrorDialog: function () {
      this.snackbar = this.showErrorDialog
    }
  }
}
</script>

<style scoped>

</style>
