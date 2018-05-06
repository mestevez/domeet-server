<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_attendants }} ({{ meetData.attendants.length }})</v-flex>
          </v-layout>
        </div>
        <v-list>
          <v-list-tile v-for="user in meetData.attendants" :key="user.user_id.user_id">
            <v-list-tile-title>{{ user.user_id.user_firstname }} {{ user.user_id.user_lastname }}</v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetAttendantsList',
  props: ['meetData', 'i18nData'],
  data () {
    return {
      i18n: Object.assign({
        title_attendants: 'Attendants'
      }, this.i18nData)
    }
  }
}
</script>

<style scoped>

</style>
