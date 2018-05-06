<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_attendants }} ({{ attendantRates.length }})</v-flex>
          </v-layout>
        </div>
        <v-list>
          <v-list-tile v-for="user in attendantRates" :key="user.user_id.user_id">
            <v-list-tile-title>{{ user.user_id.user_firstname }} {{ user.user_id.user_lastname }}</v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
export default {
  name: 'MeetAttendantsRates',
  props: ['meetData', 'i18nData'],
  computed: {
    attendantRates: function () {
      return this.meetData.attendants.filter((attd) => { return attd.attd_comment != null })
    }
  },
  data () {
    return {
      i18n: Object.assign({
        title_attendants: 'Attendants rates'
      }, this.i18nData)
    }
  }
}
</script>

<style scoped>

</style>
