import { Model, Collection } from 'vue-mc'
import SubjectNote from '@/model/subject_note'

/**
 * Subject model
 */
export default class Subject extends Model {
  options () {
    return {
      identifier: 'subject_id'
    }
  }

  // Attribute mutations.
  mutations () {
    return {
      subject_duration: Number,
      notes: (notes) => new NotesList().add(notes)
    }
  }

  routes () {
    return {
      save: '/app/meet/subject/{subject_id}',
      delete: '/app/meet/subject/{subject_id}'
    }
  }
}

export let SubjectPriority = {
  IRRELEVANT: 0,
  NORMAL: 1,
  ESSENTIAL: 2
}

class NotesList extends Collection {
  model () {
    return SubjectNote
  }

  routes () {
    return {
      fetch: '/app/meet/subject/{subject_id}/notes'
    }
  }
}
