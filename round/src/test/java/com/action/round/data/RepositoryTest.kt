package com.action.round.data

import com.action.round.data.converters.TrainingConverter
import com.action.round.data.db.TrainingDao
import com.action.round.data.db.TrainingEntity
import com.action.round.data.models.Exercise
import com.action.round.data.models.Training
import com.action.round.data.repos.Repository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.concurrent.ExecutorService
import kotlin.properties.Delegates

class RepositoryTest {

    private var sut: Repository by Delegates.notNull()

    private val entities = listOf(
        TrainingEntity(
            title = "Title 1",
            exercises = listOf("Exercise 1_1", "Exercise 1_2"),
        ),
        TrainingEntity(
            title = "Title 2",
            exercises = listOf("Exercise 2_1", "Exercise 2_2"),
        ),
    )

    private val trainings = listOf(
        Training(
            title = "Title 2",
            exercises = listOf(
                Exercise(description = "Exercise 1_1"),
                Exercise(description = "Exercise 1_2"),
            ),
        ),
        Training(
            title = "Title 2",
            exercises = listOf(
                Exercise(description = "Exercise 2_1"),
                Exercise(description = "Exercise 2_2"),
            ),
        ),
    )

    private val mockTrainingConverter: TrainingConverter = mock {
        on { convert(entities[0]) } doReturn (trainings[0])
        on { convert(entities[1]) } doReturn (trainings[1])
    }
    private val mockTrainingDao: TrainingDao = mock {
        on { getAll() } doReturn (entities)
    }
    private val mockExecutorService: ExecutorService = mock {
        on { execute(any()) } doAnswer {
            (it.getArgument(0) as? Runnable)?.run()
            null
        }
    }

    @Before
    fun setUp() {
        sut = Repository(
            trainingConverter = mockTrainingConverter,
            trainingDao = mockTrainingDao,
            es = mockExecutorService,
        )
    }

    @Test
    fun getAll_trainingsExist_verifyDaoGetAllCalledAndAssertValidResultTrainingsList() {
        var actualTrainings = emptyList<Training>()
        val onTrainingsLoaded: (List<Training>) -> Unit = {
            actualTrainings = it
        }
        sut.getAll(onTrainingsLoaded)
        verify(mockTrainingDao).getAll()
        Assert.assertEquals(trainings, actualTrainings)
    }

    @Test
    fun save_notEmptyTitleAndExercises_verifyDaoInsertCalled() {
        // todo
    }

    @Test
    fun update() {
        // todo
    }

    @Test
    fun delete() {
        // todo
    }
}
