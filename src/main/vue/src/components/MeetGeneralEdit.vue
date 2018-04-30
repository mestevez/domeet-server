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
              v-model="app.meet_title"
              :rules="[() => app.meet_title.length > 0 || i18n.rule_required]"
              required
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm5>
            <v-select
              :items="app.meet_types"
              :label="i18n.label_type"
              v-model="app.meet_type"
              required
            ></v-select>
            <input type="hidden" name="meet_type" :value="app.meet_type">
          </v-flex>
          <v-flex xs12 sm6>
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
              <input type="hidden" name="meet_date" :value="meeting_date_data">
              <v-date-picker
                v-model="meeting_date_data"
                no-title
                scrollable
                :first-day-of-week="user.locale.firstdayofweek"
                :locale="user.locale.code"
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
          <v-flex xs12 sm2>
            <v-text-field
              name="meet_duration"
              :label="i18n.label_duration"
              v-model="app.meet_duration"
              type="number"
              suffix="min"
              step="5"
              min="0"
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm12>
            <v-text-field
              name="meet_description"
              :label="i18n.label_description"
              type="text"
              v-model="app.meet_description"
              multi-line
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import moment from 'moment'
const appData = window.appData || {}
export default {
  name: 'MeetGeneralEdit',
  data () {
    return {
      datepicker: false,
      timepicker: false,
      meeting_date_data: appData.app.meet_date,
      meeting_date_input: this.parseDate(appData.app.meet_date, appData.user.locale.dateformat), // Date in ISO format for date picker
      meeting_time_data: this.parseTime(appData.app.meet_date),
      app: Object.assign({
        meet_date: '',
        meet_description: '',
        meet_duration: 30,
        meet_title: '',
        meet_type: null,
        meet_types: []
      }, appData.app),
      user: Object.assign({
        locale: {
          dateformat: 'MM-DD-YYYY',
          code: 'en-en',
          firstdayofweek: 0
        }
      }, appData.user),
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
      }, appData.i18n)
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

      console.log(this.app.meet_date)
    },
    meeting_time_data: function () {
      this.buildDate()

      console.log(this.app.meet_date)
    }
  },
  methods: {
    parseDate: function (isodate, format) {
      return moment(isodate).format(format || this.user.locale.dateformat)
    },
    parseTime: function (isodate) {
      return moment(isodate).format('HH:mm')
    },
    buildDate: function () {
      let meetDate = ''
      if (this.meeting_date_input) {
        let timestr = this.meeting_time_data ? this.meeting_time_data : '00:00'

        meetDate = moment(`${this.meeting_date_input} ${timestr}`, this.user.locale.timestampformat).toISOString()
      }

      this.app.meet_date = meetDate
    }
  }
}
</script>

<style scoped>

</style>
