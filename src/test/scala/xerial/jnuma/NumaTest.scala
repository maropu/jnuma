/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xerial.jnuma

class NumaTest extends MySpec {

  "Numa" should {
    "report NUMA info" taggedAs "report" in {
      val numNodes = Numa.numNodes()
      val numCpus = Runtime.getRuntime.availableProcessors()
      logger.info(s"numa is available: ${Numa.isAvailable}")
      logger.info(s"num nodes: ${numNodes}")
      logger.info(s"num CPUs: ${numCpus}")
      for (i <- 0 until numNodes) {
        logger.info(s"node ${i} - size:${Numa.nodeSize(i)} free:${Numa.freeSize(i)}")
      }

      for (n1 <- 0 until numNodes; n2 <- n1 until numNodes) {
        logger.info(s"distance ${n1} - ${n2}: ${Numa.distance(n1, n2)}")
      }

      val toBitString: (Array[Long]) => String = (b: Array[Long]) => {
        val s = for (i <- 0 until numCpus) yield {
          if ((b(i / 64) & (1L << (i % 64))) == 0) "0" else "1"
        }
        s.mkString
      }

      val preferred = (0 until numCpus).map { cpu =>
          Numa.runOnNode(cpu % numNodes)
          Numa.setPreferred(cpu % numNodes)
          val n = Numa.getPreferredNode
          Numa.runOnAllNodes()
          n
      }
    }

    "allocate local buffer" in {
      for (i <- 0 until 3) {
        Numa.free(Numa.allocate(1024), 1024)
      }
    }

    def write(f: => Array[Int]) = {
      val r = new scala.util.Random(13)
      for(i <- 0 until 3) {
        val arr = f
        for (index <- 0 until arr.length) {
          arr(index) = r.nextInt()
        }
      }
    }

    def boundTo[U](cpu: Int)(f: => U): U = try {
      Numa.setAffinity(cpu)
      f
    } finally {
      Numa.resetAffinity
    }

    "access java array in another node" taggedAs "jarray" in {
      for (i <- 0 until Numa.numNodes()) {
        write {
          val bufSize = 1024 * 1024
          val arr = new Array[Int](bufSize)
          Numa.toNode(arr, bufSize * 4, i)
          arr
        }
      }
    }

    "set affinity" taggedAs "affinity" in {
      (0 until Numa.numCPUs()).map { cpu =>
        boundTo(cpu) {
          write {
            assert(cpu === Numa.currentCpu)
            new Array[Int](1024 * 1024)
          }
        }
      }
    }
  }
}
