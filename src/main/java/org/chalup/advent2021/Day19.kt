package org.chalup.advent2021

import org.chalup.utils.Matrix4
import org.chalup.utils.Vector3
import org.chalup.utils.rotations3d
import java.util.LinkedList

private const val MIN_SHARED_BEACONS = 12

object Day19 {
    fun task1(input: List<String>): Int = input
        .let(this::parseScannersData)
        .let { scannersData ->
            val routes = calculateRoutes(scannersData)

            val scannersToVisit = scannersData.indices.toMutableSet()
            val beacons = mutableSetOf<Vector3>()
            val enqueuedRoutes = LinkedList<Route>().apply { add(Route(0, Matrix4.IDENTITY)) }
            while (enqueuedRoutes.isNotEmpty() && scannersToVisit.isNotEmpty()) {
                val route = enqueuedRoutes.poll()

                if (route.destinationIndex !in scannersToVisit) continue

                scannersToVisit.remove(route.destinationIndex)
                scannersData[route.destinationIndex].beaconVectors.mapTo(beacons) { (it.toVector4() * route.transform).toVector3() }

                routes[route.destinationIndex]
                    .orEmpty()
                    .filter { it.destinationIndex in scannersToVisit }
                    .mapTo(enqueuedRoutes) { it.copy(transform = it.transform * route.transform) }
            }

            check(scannersToVisit.isEmpty())

            beacons
        }
        .size

    fun task2(input: List<String>): Long = input
        .let(this::parseScannersData)
        .let { scannersData ->
            val routes = calculateRoutes(scannersData)

            val scannersLocations = mutableMapOf<Int, Vector3>()
            val enqueuedRoutes = LinkedList<Route>().apply { add(Route(0, Matrix4.IDENTITY)) }
            while (enqueuedRoutes.isNotEmpty() && scannersLocations.size < scannersData.size) {
                val route = enqueuedRoutes.poll()

                if (route.destinationIndex in scannersLocations) continue
                scannersLocations[route.destinationIndex] = (Vector3(0, 0, 0).toVector4() * route.transform).toVector3()

                routes[route.destinationIndex]
                    .orEmpty()
                    .filterNot { it.destinationIndex in scannersLocations }
                    .mapTo(enqueuedRoutes) { it.copy(transform = it.transform * route.transform) }
            }

            check(scannersLocations.size == scannersData.size)

            scannersLocations
                .flatMap { (indexA, scannerA) ->
                    scannersLocations
                        .filter { (indexB, _) -> indexB > indexA }
                        .map { (_, scannerB) ->
                            scannerA manhattanDistance scannerB
                        }
                }
                .maxOrNull()!!
        }

    private fun calculateRoutes(scannersData: List<ScannerData>): Map<Int, List<Route>> {
        val perspectives = buildList {
            scannersData.forEachIndexed { scannerIndex, (beacons) ->
                beacons.forEachIndexed { beaconIndex, beacon ->
                    add(
                        BeaconPerspective(
                            scannerIndex,
                            beaconIndex,
                            beacons.mapNotNull { otherBeacon ->
                                otherBeacon
                                    .takeUnless { it === beacon }
                                    ?.let { beacon - it }
                            }
                        )
                    )
                }
            }
        }

        return perspectives
            .asSequence()
            // preliminary matching beacons sets, based only on the relative distances between beacons
            .flatMap { perspective ->
                perspectives
                    .asSequence()
                    .filter { it.scannerIndex > perspective.scannerIndex }
                    .map { perspective to it }
            }
            .filter { (one, other) -> (one.otherBeaconsSquaredDistances intersect other.otherBeaconsSquaredDistances).size >= MIN_SHARED_BEACONS - 1 }
            // in theory, this distinctBy could lead to losing some connections (I think?) in case the distances would match, but
            // the vectors would not, and there would be some other matching perspectives pair with the same scanner indices.
            .distinctBy { (one, other) -> one.scannerIndex to other.scannerIndex }
            // full matching
            .flatMap { (one, other) ->
                // TODO: I think this could be replaced with the matrix inversion?
                sequenceOf(
                    one to other,
                    other to one,
                )
            }
            .flatMapTo(mutableSetOf()) { (one, other) ->
                rotations3d
                    .mapNotNull { rotation ->
                        val setA = one.otherBeacons
                        val setB = other.otherBeacons.map { (it.toVector4() * rotation).toVector3() }

                        val intersection = setA intersect setB

                        if (intersection.size >= MIN_SHARED_BEACONS - 1) {
                            val translateDestination = Matrix4.translation(-scannersData[other.scannerIndex].beaconVectors[other.beaconIndex])
                            val translateSource = Matrix4.translation(scannersData[one.scannerIndex].beaconVectors[one.beaconIndex])

                            ScannersConnection(
                                sourceIndex = one.scannerIndex,
                                destinationIndex = other.scannerIndex,
                                transform = translateDestination * rotation * translateSource
                            )
                        } else {
                            null
                        }
                    }
            }
            .groupBy { it.sourceIndex }
            .mapValues { (_, connections) ->
                connections.map { (_, destinationIndex, transform) -> Route(destinationIndex, transform) }
            }
    }

    private fun parseScannersData(input: List<String>) = buildList {
        val iterator = input.iterator()

        while (iterator.hasNext()) {
            iterator.next().also { check(it.startsWith("---")) }

            val beaconVectors = buildList {
                while (iterator.hasNext()) {
                    val line = iterator.next()

                    if (line.isBlank()) break

                    line.split(',')
                        .map(String::toLong)
                        .also { (x, y, z) -> add(Vector3(x, y, z)) }
                }
            }

            add(ScannerData(beaconVectors))
        }
    }

    data class ScannerData(val beaconVectors: List<Vector3>)

    data class ScannersConnection(
        val sourceIndex: Int,
        val destinationIndex: Int,
        val transform: Matrix4,
    )

    data class Route(
        val destinationIndex: Int,
        val transform: Matrix4,
    )

    data class BeaconPerspective(
        val scannerIndex: Int,
        val beaconIndex: Int,
        val otherBeacons: List<Vector3>,
    ) {
        val otherBeaconsSquaredDistances: List<Long> = otherBeacons.map {
            it.x * it.x + it.y * it.y + it.z * it.z
        }
    }
}
