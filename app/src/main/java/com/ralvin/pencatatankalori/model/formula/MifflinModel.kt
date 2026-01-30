package com.ralvin.pencatatankalori.model.formula

class MifflinModel {
	companion object {
		fun calculateRMR(weight: Double, height: Double, age: Int, isMale: Boolean): Double {
			return if (isMale) {
				(10 * weight) + (6.25 * height) - (5 * age) + 5
			} else {
				(10 * weight) + (6.25 * height) - (5 * age) - 161
			}
		}

		private var granularityValue = 0

		fun adjustTargetCalorie(newValue: Int) {
			this.granularityValue = newValue
		}

	fun getGranularityValue(): Int {
		return granularityValue
	}

	fun calculateDailyCaloriesTarget(
		rmr: Double,
		activityLevel: ActivityLevel,
		granularityValue: Int
	): Double {
		val computedCalories = (rmr * activityLevel.multiplier) + granularityValue
		return computedCalories
	}

	fun calculateDailyCalories(
		weight: Float,
		height: Float,
		age: Int,
		isMale: Boolean,
		activityLevel: ActivityLevel,
		granularityValue: Int = getGranularityValue()
	): Int {
		val rmr = calculateRMR(
			weight = weight.toDouble(),
			height = height.toDouble(),
			age = age,
			isMale = isMale
		)
		val dailyCaloriesTarget =
			calculateDailyCaloriesTarget(rmr, activityLevel, granularityValue)
		return dailyCaloriesTarget.toInt()
	}		fun calculateRemainingCalories(
			dailyCalorieTarget: Int,
			caloriesConsumed: Int,
			caloriesBurned: Int,
		): Int {
			return dailyCalorieTarget - caloriesConsumed + caloriesBurned
		}

		fun calculateNetCalories(
			caloriesConsumed: Int,
			caloriesBurned: Int,
		): Int {
			val rawNet = caloriesConsumed - caloriesBurned
			return maxOf(0, rawNet)
		}
	}
}
