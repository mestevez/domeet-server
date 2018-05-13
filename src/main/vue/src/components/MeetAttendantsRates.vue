<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">
          <v-layout>
            <v-flex>{{ i18n.title_attendants }} ({{ attendantRates.length }})</v-flex>
          </v-layout>
        </div>
        <v-list class="fit-content">
          <v-list-tile v-for="(attd, index) in attendantRates.toJSON()" :key="attd.user_id.user_id">
            <v-divider v-if="index &gt; 0" :key="index"></v-divider>
            <v-list-tile-content>
                <v-list-tile-title>
                  <v-layout row wrap>
                    <v-flex xs12 pb-0>{{ attd.user_id.user_firstname }} {{ attd.user_id.user_lastname }}</v-flex>
                    <v-flex xs12 pt-0>
                      <v-icon color="warning">{{ getIconStar(1, attd.attd_rate) }}</v-icon>
                      <v-icon color="warning">{{ getIconStar(2, attd.attd_rate) }}</v-icon>
                      <v-icon color="warning">{{ getIconStar(3, attd.attd_rate) }}</v-icon>
                      <v-icon color="warning">{{ getIconStar(4, attd.attd_rate) }}</v-icon>
                      <v-icon color="warning">{{ getIconStar(5, attd.attd_rate) }}</v-icon>
                    </v-flex>
                  </v-layout>
                </v-list-tile-title>
              <v-list-tile-sub-title v-html="attd.attd_comment"></v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import '../resources/css/generic.css'

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
  },
  methods: {
    getIconStar: function (range, value) {
      let icon

      if (value && value >= range) {
        icon = 'star'
      } else if (value && value >= range - 0.5) {
        icon = 'star_half'
      } else {
        icon = 'star_border'
      }

      return icon
    }
  }
}
</script>

<style scoped>

</style>
