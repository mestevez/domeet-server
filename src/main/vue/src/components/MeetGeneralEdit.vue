<template>
  <v-card>
      <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">{{ i18n.title_general }}</div>
        <v-layout row wrap>
          <v-flex xs12 sm7>
            <v-text-field
              name="meet_title"
              :label="i18n.label_title"
              type="text"
              v-model="meetData.meet_title"
              :rules="[() => meetData.meet_title.length > 0 || i18n.rule_required]"
              required
              @input="updateMeet"
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm5>
            <v-select
              :items="meetTypes"
              :label="i18n.label_type"
              v-model="meetData.meet_type"
              required
              @change="updateMeet"
            ></v-select>
            <input type="hidden" name="meet_type" :value="meetData.meet_type">
          </v-flex>
          <v-flex xs12 sm5>
            <v-menu
              ref="datepicker"
              lazy
              :close-on-content-click="false"
              v-model="datepicker"
              transition="scale-transition"
              offset-y
              full-width
              :nudge-right="40"
              min-width="290px"
              :return-value.sync="meeting_date_data"
            >
              <v-text-field
                slot="activator"
                :label="i18n.label_date"
                v-model="meeting_date_input"
                readonly
                :rules="[() => meeting_date_input.length > 0 || i18n.rule_required]"
                required
              ></v-text-field>
              <input type="hidden" name="meet_date" :value="this.meetData.meet_dates[0].meet_date">
              <v-date-picker
                v-model="meeting_date_data"
                no-title
                scrollable
                :first-day-of-week="locale.firstdayofweek"
                :locale="locale.code"
              >
                <v-spacer></v-spacer>
                <v-btn flat color="primary" @click="datepicker = false">{{ i18n.btn_cancel }}</v-btn>
                <v-btn flat color="primary" @click="$refs.datepicker.save(meeting_date_data)">{{ i18n.btn_ok }}</v-btn>
              </v-date-picker>
            </v-menu>
          </v-flex>
          <v-flex xs12 sm4>
            <v-menu
              ref="timepicker"
              lazy
              :close-on-content-click="false"
              v-model="timepicker"
              transition="scale-transition"
              offset-y
              full-width
              :nudge-right="40"
              min-width="290px"
              :return-value.sync="meeting_time_data"
            >
              <v-text-field
                slot="activator"
                :label="i18n.label_time"
                v-model="meeting_time_data"
                readonly
                :rules="[() => meeting_time_data.length > 0 || i18n.rule_required]"
                required
              ></v-text-field>
              <v-time-picker
                v-model="meeting_time_data"
                format="24hr"
                scrollable
              >
                <v-spacer></v-spacer>
                <v-btn flat color="primary" @click="timepicker = false">{{ i18n.btn_cancel }}</v-btn>
                <v-btn flat color="primary" @click="$refs.timepicker.save(meeting_time_data)">{{ i18n.btn_ok }}</v-btn>
              </v-time-picker>
            </v-menu>
          </v-flex>
          <v-flex xs12 sm3>
            <v-text-field
              name="meet_duration"
              :label="i18n.label_duration"
              v-model="meetData.meet_duration"
              type="number"
              suffix="min"
              step="5"
              min="0"
              @input="updateMeet"
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm12>
            <v-text-field
              name="meet_description"
              :label="i18n.label_description"
              type="text"
              v-model="meetData.meet_description"
              multi-line
              @input="updateMeet"
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import moment from 'moment'

export default {
  name: 'MeetGeneralEdit',
  props: ['meetData', 'meetTypes', 'i18nData', 'locale'],
  data () {
    return {
      datepicker: false,
      timepicker: false,
      meeting_date_data: this.meetData.meet_dates[0].meet_date,
      meeting_date_input: this.parseDate(this.meetData.meet_dates[0].meet_date, this.locale.dateformat), // Date in ISO format for date picker
      meeting_time_data: this.parseTime(this.meetData.meet_dates[0].meet_date),
      updateTimeout: null,
      onUpdate: false,

      i18n: Object.assign({
        title_general: 'General description',
        label_title: 'Title',
        label_type: 'Type',
        label_date: 'Date',
        label_time: 'Time',
        label_duration: 'Duration',
        label_description: 'Description',
        btn_cancel: 'Cancel',
        btn_ok: 'Ok',
        rule_required: 'This field is required'
      }, this.i18nData)
    }
  },
  watch: {
    meeting_date_data: function (isodate) {
      if (isodate) {
        this.meeting_date_input = this.parseDate(isodate)
      } else {
        this.meeting_date_input = ''
      }
      this.buildDate()
    },
    meeting_time_data: function () {
      this.buildDate()
    }
  },
  methods: {
    parseDate: function (isodate, format) {
      return moment(isodate).format(format || this.locale.dateformat)
    },
    parseTime: function (isodate) {
      return moment(isodate).format('HH:mm')
    },
    buildDate: function () {
      let meetDate = ''
      if (this.meeting_date_input) {
        let timestr = this.meeting_time_data ? this.meeting_time_data : '00:00'

        meetDate = moment(`${this.meeting_date_input} ${timestr}`, this.locale.timestampformat).toISOString()
      }

      this.meetData.meet_dates[0].meet_date = meetDate
      this.updateMeet()
    },
    updateMeet: function () {
      if (this.updateTimeout != null) {
        clearTimeout(this.updateTimeout)
      }

      this.updateTimeout = setTimeout(() => {
        this.updateTimeout = null
        this.onUpdate = true
        this.meetData.save().then(() => {
          this.onUpdate = false
        }).catch(() => {
          this.onUpdate = false
        })
      }, 500)
    }
  }
}
</script>

<style scoped>

</style>
