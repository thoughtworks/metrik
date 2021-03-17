package fourkeymetrics.common.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<EnumConstraint, String> {
    private lateinit var valueList: MutableList<String>

    override fun initialize(constraintAnnotation: EnumConstraint) {
        valueList = mutableListOf()
        constraintAnnotation.acceptedValues.forEach { valueList.add(it.toUpperCase()) }
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if (value.isNullOrBlank()) {
            false
        } else {
            valueList.contains(value.toUpperCase())
        }
    }
}