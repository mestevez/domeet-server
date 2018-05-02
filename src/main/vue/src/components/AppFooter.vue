<template>
  <v-footer dark color="primary" app height="auto">
    <span>&copy; 2018</span>
    <v-spacer></v-spacer>
    <div>
      <slot name="actions"></slot>
    </div>
  </v-footer>
</template>

<script>
export default {
  name: 'AppFooter'
}
</script>

<style scoped>

</style>
