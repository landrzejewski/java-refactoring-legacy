package pl.training.workshop.exercises.e06_movieinfo

/** Stabilny kontrakt sceny: kto pyta o film. Dane o filmach ogląda tylko personel kina. */
data class User(val name: String, val staff: Boolean) {
    companion object {
        fun staff(name: String): User = User(name, true)

        fun guest(name: String): User = User(name, false)
    }
}
