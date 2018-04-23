<template>
  <v-app id="home">
    <apptoolbar></apptoolbar>
    <v-content>
      Hello
    </v-content>
    <v-footer dark color="primary" app>
      <span>&copy; 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
import AppToolbar from '@/components/AppToolbar'

export default {
  name: 'Home',
  components: {
    'apptoolbar': AppToolbar
  }
}
</script>

<style scoped>

</style>
