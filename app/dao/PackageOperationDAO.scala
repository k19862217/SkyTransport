package dao

/**
  * Created by xiaochen.tian on 12/2/15.
  */

import controllers.OperationController
import enumerate.OperationType
import models.Tables._
import models.tables.MyDriver.api._
import models.{PackageOperation, PackageInfo}
import scala.concurrent.ExecutionContext.Implicits.global

object PackageOperationDAO extends BaseDAO{

  def divide_operation(originPackageId:Int,
                       userId: Int,
                       dividePackage:(OperationController.dividePackageClass)) = {
    PackageInfoDAO.get_by_id(originPackageId) map { case Some(origin)=>
      dividePackage.targetPackage foreach { target =>
        PackageInfoDAO.insert_return_id(PackageInfo(
          0,
          Some(userId),
          None,
          origin.warehouseId,
          Some(target.map { o => o.name + ":(" + o.count + ")" }.mkString("/")),
          Some(target.map(_.price).sum),
          Some(target.map(_.count).sum),
          origin.orderNumber,
          origin.forecastTime,
          None,
          None,
          origin.arrivedTime,
          origin.status,
          None,
          None,
          None
        )) map { id =>
          insert_return_id(PackageOperation(0, originPackageId, id, OperationType.PACKAGE_DIVIDE, 0, Option(userId)))
        }
      }
    }

    PackageInfoDAO.change_status(originPackageId, "4")
  }

  def combine_operation (combinePackage: OperationController.combinePackageClass, userId: Int) = {
    PackageInfoDAO.get_by_multiple_id(combinePackage.originPackageIDs.map(_.toInt)) map { case x => {
      PackageInfoDAO.insert_return_id(PackageInfo(
        0,
        Some(userId),
        None,
        None,
        Some(x.map(_.goodsName.getOrElse("")).mkString("/")),
        Some(x.map(_.goodsPrice.getOrElse(0.0)).sum),
        Some(x.map(_.goodsCount.getOrElse(0)).sum),
        None,
        None,
        None,
        None,
        None,
        Some(x.map(_.status.getOrElse("0")).min),
        None,
        None,
        None
      )) map { id =>
        x.foreach { y =>
          insert_return_id(PackageOperation(0, y.id, id, OperationType.PACKAGE_COMBINE, 0, Option(userId)))
          PackageInfoDAO.change_status(y.id, "7")
        }
      }
    }
    }
  }

  def insert_return_id (packageOperation: PackageOperation) = {
    val q = (PackageOperationTable returning PackageOperationTable.map(_.id)) += packageOperation
    db.run( q )
  }
}
