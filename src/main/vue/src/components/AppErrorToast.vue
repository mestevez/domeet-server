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
  props: ['message'],
  data () {
    return {
      snackbar: this.message != null,
      i18n: {
        'btn_close': 'Close'
      }
    }
  },
  watch: {
    message: function () {
      console.log('message changed')
      this.snackbar = this.message != null
    }
  }
}
</script>

<style scoped>

</style>
