package com.omnitech.chai.scripts

import com.omnitech.chai.model.CustomerSegment

/**
 * Created by kay on 10/27/14.
 */
class SegmetScripts {

    def SegA = { CustomerSegment cs, Double customerScore ->

        customerScore > 2

    }

    def SegB = { CustomerSegment cs, Double customerScore ->

        customerScore >= 1.5 && customerScore < 2

    }

    def SegC = { CustomerSegment cs, Double customerScore ->

        customerScore >= 1 && customerScore < 1.5

    }

    def SegD = { CustomerSegment cs, Double customerScore ->

        customerScore >= 0 && customerScore < 1

    }
}
